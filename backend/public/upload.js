const fileInput = document.getElementById('videoFile');
const uploadBtn = document.getElementById('uploadBtn');
const progressBar = document.getElementById('progressBar');
const statusText = document.getElementById('status');
const cancelBtn = document.getElementById('cancelBtn');
const categoryInput = document.getElementById('category'); // select or input

let controller = new AbortController();

uploadBtn.addEventListener('click', async () => {
  const file = fileInput.files[0];
  if (!file) {
    alert('Please select a video file!');
    return;
  }

  controller = new AbortController();
  const chunkSize = 10 * 1024 * 1024; // 10MB
  const totalChunks = Math.ceil(file.size / chunkSize);
  const filename = file.name;
  const category = categoryInput.value || "Movies"; // fallback

  statusText.textContent = 'Uploading...';

  for (let i = 0; i < totalChunks; i++) {
    const start = i * chunkSize;
    const end = Math.min(file.size, start + chunkSize);
    const chunk = file.slice(start, end);
    const formData = new FormData();
    formData.append('chunk', chunk, `${filename}-chunk${i}`);

    try {
      await fetch('/api/upload-chunk', {
        method: 'POST',
        body: formData,
        signal: controller.signal,
      });

      const pct = Math.round(((i + 1) / totalChunks) * 100);
      progressBar.value = pct;
      statusText.textContent = `Uploading ${pct}% (${i + 1}/${totalChunks})`;
    } catch (err) {
      statusText.textContent = 'Upload canceled or failed';
      console.error(err);
      return;
    }
  }

  // Merge with category
  const mergeRes = await fetch('/api/merge', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ filename, totalChunks, category }),
  });

  const mergeData = await mergeRes.json();
  if (mergeData.message) {
    statusText.textContent = '✅ Upload complete!';
    console.log('Merged video:', mergeData.file);
  } else {
    statusText.textContent = '❌ Merge failed';
  }
});

cancelBtn.addEventListener('click', () => {
  controller.abort();
  statusText.textContent = 'Upload canceled!';
});
