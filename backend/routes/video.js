// routes/video.js
const express = require('express');
const fs = require('fs');
const path = require('path');
const multer = require('multer');
const ffmpeg = require('fluent-ffmpeg');
const ffmpegPath = require('ffmpeg-static');

ffmpeg.setFfmpegPath(ffmpegPath);

const router = express.Router();

// Folder paths
const uploadPath = path.join(__dirname, '../uploads'); // merged videos
const chunkPath = path.join(__dirname, '../chunks');   // temporary chunks
const metaFile = path.join(uploadPath, 'videos.json');

// Ensure directories exist
if (!fs.existsSync(uploadPath)) fs.mkdirSync(uploadPath, { recursive: true });
if (!fs.existsSync(chunkPath)) fs.mkdirSync(chunkPath, { recursive: true });
if (!fs.existsSync(metaFile)) fs.writeFileSync(metaFile, JSON.stringify([]));

// --------------------------
// Multer storage for chunks
// --------------------------
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, chunkPath),
  filename: (req, file, cb) => cb(null, file.originalname),
});
const upload = multer({ storage });

// --------------------------
// Upload individual chunks
// --------------------------
router.post('/upload-chunk', upload.single('chunk'), (req, res) => {
  console.log(`📦 Received chunk: ${req.file.originalname}`);
  res.json({ message: 'Chunk received successfully' });
});

// --------------------------
// Merge chunks into a full video
// --------------------------
router.post('/merge', async (req, res) => {
  try {
    const { filename, totalChunks, category } = req.body;

    if (!filename || !totalChunks) {
      return res.status(400).json({ error: 'Missing filename or totalChunks' });
    }

    const mergedPath = path.join(uploadPath, filename);
    const writeStream = fs.createWriteStream(mergedPath);

    console.log(`🧩 Starting merge of ${totalChunks} chunks into ${mergedPath}`);

    for (let i = 0; i < totalChunks; i++) {
      const chunkFile = path.join(chunkPath, `${filename}-chunk${i}`);
      if (!fs.existsSync(chunkFile)) {
        return res.status(400).json({ error: `Missing chunk ${i}` });
      }

      const data = fs.readFileSync(chunkFile);
      writeStream.write(data);
      fs.unlinkSync(chunkFile); // cleanup after merging
    }

    writeStream.end();

    writeStream.on('finish', async () => {
      console.log(`✅ Merged video saved as ${filename}`);

      // Save metadata with category
      const videos = JSON.parse(fs.readFileSync(metaFile));
      videos.push({
        filename,
        url: `/api/stream/${filename}`,
        category: category || "Movies"
      });
      fs.writeFileSync(metaFile, JSON.stringify(videos, null, 2));

      res.json({
        message: 'File merged successfully',
        file: filename,
        path: `/api/stream/${filename}`
      });
    });

  } catch (err) {
    console.error('❌ Error merging:', err);
    res.status(500).json({ error: err.message });
  }
});

// --------------------------
// Stream video endpoint
// --------------------------
router.get('/stream/:filename', (req, res) => {
  const file = path.join(uploadPath, req.params.filename);

  if (!fs.existsSync(file)) {
    return res.status(404).json({ error: 'Video not found' });
  }

  const stat = fs.statSync(file);
  const fileSize = stat.size;
  const range = req.headers.range;

  if (!range) {
    res.setHeader('Content-Length', fileSize);
    res.setHeader('Content-Type', 'video/mp4');
    fs.createReadStream(file).pipe(res);
    return;
  }

  const parts = range.replace(/bytes=/, '').split('-');
  const start = parseInt(parts[0], 10);
  const end = parts[1] ? parseInt(parts[1], 10) : fileSize - 1;
  const chunksize = end - start + 1;

  console.log(`🎥 Streaming bytes ${start}-${end} of ${req.params.filename}`);

  const fileStream = fs.createReadStream(file, { start, end });
  const head = {
    'Content-Range': `bytes ${start}-${end}/${fileSize}`,
    'Accept-Ranges': 'bytes',
    'Content-Length': chunksize,
    'Content-Type': 'video/mp4',
  };

  res.writeHead(206, head);
  fileStream.pipe(res);
});

// --------------------------
// List all uploaded videos (with category)
// --------------------------
router.get('/videos', (req, res) => {
  try {
    const videos = JSON.parse(fs.readFileSync(metaFile));
    res.json(videos);
  } catch (err) {
    console.error('❌ Error listing videos:', err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
