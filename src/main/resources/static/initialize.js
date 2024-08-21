function initializeProcess(event) {
    const files = event.dataTransfer.files;
    if (files.length > 0) {
        const file = files[0];

        fetch('/subtitles/initialize', { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                id = data.id;
                uploadFile(file, id);
            });

        const url = URL.createObjectURL(file);
        videoPlayer.src = url;
        videoPlayer.style.display = 'block';
        dropZone.style.display = 'none';
        videoContainer.style.display = 'block';
        playPauseBtn.textContent = 'Play';

        const userInput = input.value;
        fetch('/processInput', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ input: userInput })
        })
            .then(response => response.json())
            .then(data => {
                storedResponse = data.message;
                const textOverlay = document.getElementById('textOverlay');
                textOverlay.textContent = storedResponse[0];
                createMarkers();
            });
    }
}