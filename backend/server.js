// server.js
const express = require('express');
const morgan = require('morgan');
const cors = require('cors');
const path = require('path');
const videoRouter = require('./routes/video');

const app = express();
const PORT = process.env.PORT || 8000;

// Middleware
app.use(express.json({ limit: '600mb' }));
app.use(morgan('dev'));
app.use(cors());

// Serve static frontend (HTML, CSS, JS)
app.use(express.static(path.join(__dirname, 'public')));

// Routes
app.use('/api', videoRouter);

// Global error handler
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  if (err && err.name === 'MulterError') {
    return res.status(400).json({ error: err.message });
  }
  res.status(500).json({ error: err.message || 'Internal Server Error' });
});

app.listen(PORT, "0.0.0.0", () => {
  console.log("`🚀 Server running on http://10.87.124.69:8000");

});

