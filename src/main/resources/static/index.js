let storedResponse = [];
const input = document.getElementById('userInput');
const sliderValues = [];
let id;

document.addEventListener("DOMContentLoaded", function () {
    const dropZone = document.getElementById('dropZone');
    const videoContainer = document.getElementById('videoContainer');
    const videoPlayer = document.getElementById('videoPlayer');
    const playPauseBtn = document.getElementById('playPauseBtn');
    const seekBar = document.getElementById('seekBar');
    const proceedButton = document.getElementById('proceed');

    dropZone.addEventListener('dragover', (event) => {
        event.preventDefault();
        dropZone.classList.add('dragover');
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.classList.remove('dragover');
    });

    dropZone.addEventListener('drop', (event) => {
        event.preventDefault();
        dropZone.classList.remove('dragover');
        initializeProcess(event);
    });

    videoPlayer.style.display = 'none';

    playPauseBtn.addEventListener('click', () => {
        if (videoPlayer.paused || videoPlayer.ended) {
            videoPlayer.play();
            playPauseBtn.textContent = 'Pause';
        } else {
            videoPlayer.pause();
            playPauseBtn.textContent = 'Play';
        }
    });

    videoPlayer.addEventListener('timeupdate', () => {
        seekBar.value = (videoPlayer.currentTime / videoPlayer.duration) * 100;
        const textOverlay = document.getElementById('textOverlay');
        const textIndex = findIndexInRange(seekBar.value, sliderValues);
        textOverlay.textContent = storedResponse[textIndex];
    });

    seekBar.addEventListener('input', () => {
        const seekTime = (seekBar.value / 100) * videoPlayer.duration;
        videoPlayer.currentTime = seekTime;
    });

    proceedButton.addEventListener('click', () => {
        startProcessing();
    });

    function findIndexInRange(value, arr) {
        const sortedArr = arr.slice().sort((a, b) => a - b);

        if (value * 10 < sortedArr[0] * 10) {
            return -1;
        }

        for (let i = 1; i < sortedArr.length; i++) {
            if (value >= sortedArr[i - 1] && value < sortedArr[i]) {
                return i - 1;
            }
        }
        return -1;
    }
});
