// routes/video.js  (Backblaze B2 / Cloudflare R2 storage + MongoDB metadata)
const express = require('express');
const fs = require('fs');
const os = require('os');
const path = require('path');
const { Readable } = require('stream');
const multer = require('multer');
const mongoose = require('mongoose');
const {
  S3Client,
  GetObjectCommand,
  DeleteObjectCommand,
} = require('@aws-sdk/client-s3');
const { Upload } = require('@aws-sdk/lib-storage');

const router = express.Router();

// --------------------------
// Config
// --------------------------
const {
  MONGO_URI,
  S3_ENDPOINT,
  S3_REGION,
  S3_BUCKET,
  S3_KEY_ID,
  S3_SECRET,
  UPLOAD_KEY,
} = process.env;

const missing = ['MONGO_URI', 'S3_ENDPOINT', 'S3_REGION', 'S3_BUCKET', 'S3_KEY_ID', 'S3_SECRET']
  .filter((k) => !process.env[k]);
if (missing.length) console.error('❌ Missing env vars:', missing.join(', '));

// --------------------------
// MongoDB
// --------------------------
const videoSchema = new mongoose.Schema({
  filename: { type: String, required: true, unique: true },
  key: { type: String, required: true },
  url: { type: String, required: true },
  category: { type: String, default: 'Movies' },
  size: Number,
  createdAt: { type: Date, default: Date.now },
});
const Video = mongoose.models.Video || mongoose.model('Video', videoSchema);

if (MONGO_URI) {
  mongoose
    .connect(MONGO_URI)
    .then(() => console.log('✅ MongoDB connected'))
    .catch((err) => console.error('❌ MongoDB connection error:', err.message));
}

// --------------------------
// S3-compatible client (B2 / R2)
// --------------------------
const s3 = new S3Client({
  endpoint: S3_ENDPOINT,
  region: S3_REGION || 'auto',
  credentials: { accessKeyId: S3_KEY_ID, secretAccessKey: S3_SECRET },
  forcePathStyle: true,
  // Newer SDK versions add CRC32 checksums by default, which B2 and some
  // S3-compatible stores reject. Only send them when required.
  requestChecksumCalculation: 'WHEN_REQUIRED',
  responseChecksumValidation: 'WHEN_REQUIRED',
});

// --------------------------
// Helpers
// --------------------------
const chunkPath = path.join(os.tmpdir(), 'playbox-chunks');
fs.mkdirSync(chunkPath, { recursive: true });

// Strip any directory parts so a filename can't escape the chunk folder.
const safeName = (name) => path.basename(String(name || ''));

// Optional API key check for upload endpoints.
function requireKey(req, res, next) {
  if (!UPLOAD_KEY) return next();
  if (req.get('x-api-key') === UPLOAD_KEY) return next();
  return res.status(401).json({ error: 'Unauthorized' });
}

// --------------------------
// Multer: temporary chunk storage on local disk
// --------------------------
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, chunkPath),
  filename: (req, file, cb) => cb(null, safeName(file.originalname)),
});
const upload = multer({ storage });

// --------------------------
// Health check
// --------------------------
router.get('/health', (req, res) => {
  res.json({ ok: true });
});

// --------------------------
// Upload individual chunks
// --------------------------
router.post('/upload-chunk', requireKey, upload.single('chunk'), (req, res) => {
  if (!req.file) return res.status(400).json({ error: 'No chunk received' });
  console.log(`📦 Received chunk: ${req.file.filename}`);
  res.json({ message: 'Chunk received successfully' });
});

// --------------------------
// Merge chunks and stream them straight to the bucket
// --------------------------
router.post('/merge', requireKey, async (req, res) => {
  const filename = safeName(req.body && req.body.filename);
  const total = parseInt(req.body && req.body.totalChunks, 10);
  const category = (req.body && req.body.category) || 'Movies';

  if (!filename || !Number.isInteger(total) || total < 1) {
    return res.status(400).json({ error: 'Missing filename or totalChunks' });
  }

  const chunkFiles = [];
  for (let i = 0; i < total; i++) {
    const f = path.join(chunkPath, `${filename}-chunk${i}`);
    if (!fs.existsSync(f)) {
      return res.status(400).json({ error: `Missing chunk ${i}` });
    }
    chunkFiles.push(f);
  }

  const key = `videos/${Date.now()}-${filename}`;

  try {
    console.log(`🧩 Merging ${total} chunks into ${key}`);

    // Read chunks one after another as a single stream (low memory use).
    const body = Readable.from(
      (async function* () {
        for (const f of chunkFiles) {
          for await (const buf of fs.createReadStream(f)) yield buf;
        }
      })()
    );

    const uploader = new Upload({
      client: s3,
      params: { Bucket: S3_BUCKET, Key: key, Body: body, ContentType: 'video/mp4' },
      queueSize: 2,
      partSize: 8 * 1024 * 1024,
    });
    await uploader.done();

    // Replace any earlier upload with the same filename.
    const existing = await Video.findOne({ filename });
    const size = chunkFiles.reduce((sum, f) => sum + fs.statSync(f).size, 0);
    const doc = await Video.findOneAndUpdate(
      { filename },
      { filename, key, url: `/api/stream/${encodeURIComponent(filename)}`, category, size },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );
    if (existing && existing.key !== key) {
      s3.send(new DeleteObjectCommand({ Bucket: S3_BUCKET, Key: existing.key })).catch(() => {});
    }

    // Cleanup temp chunks only after a successful upload.
    for (const f of chunkFiles) fs.unlink(f, () => {});

    console.log(`✅ Saved ${filename}`);
    res.json({ message: 'File merged successfully', file: filename, path: doc.url });
  } catch (err) {
    console.error('❌ Error merging:', err);
    res.status(500).json({ error: err.message });
  }
});

// --------------------------
// Stream video (supports Range requests)
// --------------------------
router.get('/stream/:filename', async (req, res) => {
  try {
    const filename = safeName(decodeURIComponent(req.params.filename));
    const video = await Video.findOne({ filename }).lean();
    if (!video) return res.status(404).json({ error: 'Video not found' });

    const range = req.headers.range;
    const obj = await s3.send(
      new GetObjectCommand({
        Bucket: S3_BUCKET,
        Key: video.key,
        ...(range ? { Range: range } : {}),
      })
    );

    const headers = {
      'Content-Type': 'video/mp4',
      'Accept-Ranges': 'bytes',
      'Content-Length': obj.ContentLength,
    };
    if (range && obj.ContentRange) {
      headers['Content-Range'] = obj.ContentRange;
      res.writeHead(206, headers);
    } else {
      res.writeHead(200, headers);
    }

    // Stop reading from the bucket if the player disconnects.
    res.on('close', () => obj.Body.destroy());
    obj.Body.on('error', () => res.destroy());
    obj.Body.pipe(res);
  } catch (err) {
    if (err.name === 'NoSuchKey') return res.status(404).json({ error: 'Video not found' });
    if (err.name === 'InvalidRange' || err.$metadata?.httpStatusCode === 416) {
      return res.status(416).end();
    }
    console.error('❌ Stream error:', err);
    if (!res.headersSent) res.status(500).json({ error: err.message });
  }
});

// --------------------------
// List all videos
// --------------------------
router.get('/videos', async (req, res) => {
  try {
    const videos = await Video.find({}, 'filename url category size createdAt')
      .sort({ createdAt: 1 })
      .lean();
    res.json(videos.map(({ _id, ...v }) => v));
  } catch (err) {
    console.error('❌ Error listing videos:', err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;