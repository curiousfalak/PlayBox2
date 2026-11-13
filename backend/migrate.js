// migrate.js - one-time: copy old local videos (uploads/videos.json) to B2 + MongoDB
// Run from backend/:  node -r dotenv/config migrate.js
const fs = require('fs');
const path = require('path');
const mongoose = require('mongoose');
const { S3Client } = require('@aws-sdk/client-s3');
const { Upload } = require('@aws-sdk/lib-storage');

const { MONGO_URI, S3_ENDPOINT, S3_REGION, S3_BUCKET, S3_KEY_ID, S3_SECRET } = process.env;

const uploadDir = path.join(__dirname, 'uploads');
const metaFile = path.join(uploadDir, 'videos.json');

const Video = mongoose.model(
  'Video',
  new mongoose.Schema({
    filename: { type: String, required: true, unique: true },
    key: { type: String, required: true },
    url: { type: String, required: true },
    category: { type: String, default: 'Movies' },
    size: Number,
    createdAt: { type: Date, default: Date.now },
  })
);

const s3 = new S3Client({
  endpoint: S3_ENDPOINT,
  region: S3_REGION || 'auto',
  credentials: { accessKeyId: S3_KEY_ID, secretAccessKey: S3_SECRET },
  forcePathStyle: true,
  requestChecksumCalculation: 'WHEN_REQUIRED',
  responseChecksumValidation: 'WHEN_REQUIRED',
});

(async () => {
  if (!fs.existsSync(metaFile)) throw new Error('uploads/videos.json not found');
  await mongoose.connect(MONGO_URI);
  console.log('MongoDB connected');

  // De-duplicate by filename (last entry wins)
  const byName = new Map();
  for (const v of JSON.parse(fs.readFileSync(metaFile, 'utf8'))) byName.set(v.filename, v);

  let ok = 0, skipped = 0, failed = 0;
  for (const [filename, v] of byName) {
    const file = path.join(uploadDir, filename);
    if (!fs.existsSync(file)) {
      console.log(`SKIP (file missing): ${filename}`);
      skipped++;
      continue;
    }
    if (await Video.exists({ filename })) {
      console.log(`SKIP (already migrated): ${filename}`);
      skipped++;
      continue;
    }
    try {
      const key = `videos/${Date.now()}-${filename}`;
      const size = fs.statSync(file).size;
      console.log(`Uploading ${filename} (${(size / 1048576).toFixed(1)} MB)...`);
      await new Upload({
        client: s3,
        params: { Bucket: S3_BUCKET, Key: key, Body: fs.createReadStream(file), ContentType: 'video/mp4' },
        partSize: 8 * 1024 * 1024,
      }).done();
      await Video.create({
        filename,
        key,
        url: `/api/stream/${encodeURIComponent(filename)}`,
        category: v.category || 'Movies',
        size,
      });
      console.log(`  done: ${filename}`);
      ok++;
    } catch (err) {
      console.error(`  FAILED: ${filename}: ${err.message}`);
      failed++;
    }
  }
  console.log(`\nFinished. Migrated: ${ok}, skipped: ${skipped}, failed: ${failed}`);
  await mongoose.disconnect();
})().catch((e) => {
  console.error('Error:', e.message);
  process.exit(1);
});