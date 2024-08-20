let typingTimer;
const doneTypingInterval = 2000;
let storedResponse = [];
const input = document.getElementById('userInput');
const sliderValues = [];
let id;

document.addEventListener("DOMContentLoaded", function() {
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

        const files = event.dataTransfer.files;
        if (files.length > 0) {
            const file = files[0];

            fetch('/subtitles/initialize', {method: 'POST'})
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
                console.log("Stored Response: ", storedResponse[0]);

                const numberOfSliders = storedResponse.length;
                for (let i = 0; i < numberOfSliders; i++) {
                    sliderValues[i] = i * (100 / numberOfSliders);
                }

                for (let i = 0; i < numberOfSliders; i++) {
                    const slider = document.createElement('input');
                    slider.type = 'range';
                    slider.min = 0;
                    slider.max = 100;
                    slider.step = '0.1'
                    slider.value = i * (100 / numberOfSliders);
                    slider.classList.add('slider');
                    slider.dataset.index = i;
                    multiSliderContainer.appendChild(slider);

                    const marker = document.createElement('div');
                    marker.classList.add('marker');
                    marker.style.left = '0%';
                    marker.dataset.index = i;
                    multiSliderContainer.appendChild(marker);

                    marker.addEventListener('mousedown', (event) => {
                        const index = event.target.dataset.index;
                        function onMouseMove(event) {
                            const containerRect = multiSliderContainer.getBoundingClientRect();
                            const markerPosition = ((event.clientX - containerRect.left) / containerRect.width) * 100;
                            slider.value = Math.max(0, Math.min(100, markerPosition));
                            sliderValues[index] = slider.value;
                            updateMarkers();
                        }

                        function onMouseUp() {
                            document.removeEventListener('mousemove', onMouseMove);
                            document.removeEventListener('mouseup', onMouseUp);
                        }

                        document.addEventListener('mousemove', onMouseMove);
                        document.addEventListener('mouseup', onMouseUp);
                    });
                }

                function updateMarkers() {
                    sliderValues.forEach((value, index) => {
                        const marker = multiSliderContainer.querySelector(`.marker[data-index='${index}']`);
                        marker.style.left = `${value}%`;
                    });
                }

                updateMarkers();

            });
        }

        function connectWebSocket(id, fileName) {
            const url = `ws://localhost:8080/video/original/${id}?fileName=${encodeURIComponent(fileName)}`;
            socket = new WebSocket(url);
            socket.binaryType = 'arraybuffer';

            socket.onopen = function() {
                console.log("WebSocket connection opened");
            };

            socket.onmessage = function(event) {
                console.log("Message from server: ", event.data);
            };

            socket.onclose = function() {
                console.log("WebSocket connection closed");
                document.getElementById('proceed').disabled = false;
                document.getElementById('message').innerText = "Video upload is done!";
            };
        }

        function uploadFile(file, id) {
            const chunkSize = 1024; // 1KB per chunk
            let offset = 0;

            connectWebSocket(id, file.fileName);

            socket.onopen = () => {
                const reader = new FileReader();

                reader.onload = function(event) {
                    if (event.target.readyState === FileReader.DONE) {
                        socket.send(event.target.result);
                        offset += chunkSize;

                        const progress = Math.min((offset / file.size) * 100, 100);
                        console.log('Progress: ' + progress);
                        
                        document.getElementById('progressBar').value = progress;
                        document.getElementById('message').innerText = "Uploading video to server. Upload progress: " + Math.floor(progress) + "%";
                        document.getElementById('proceed').disabled = true;

                        if (offset < file.size) {
                            readSlice(offset);
                        } else {
                            socket.close();
                        }
                    }
                };

                const readSlice = o => {
                    const slice = file.slice(offset, o + chunkSize);
                    reader.readAsArrayBuffer(slice);
                };

                readSlice(0);
            };
        }
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
        console.log("Click");
        document.getElementById('progressBar').value = 0;
        document.getElementById('message').innerText = "Preparing for subtitles processing...";

        const textChunks = []
        for (let i = 0; i < storedResponse.length; i++) {
            const startFrame = sliderValues[i] / 100 * videoPlayer.duration * 24;
            let endFrame = sliderValues[i + 1] / 100 * videoPlayer.duration * 24;

            if(i == storedResponse.length - 1) {
                endFrame = videoPlayer.duration * 24;
            }

            textChunks[i] = {
                text: storedResponse[i],
                startFrame: startFrame,
                endFrame: endFrame
            }
        }

        const req = { 
            bottomMargin: 50,
            fontName: "Arial",
            textChunks: textChunks,
        };

        fetch('/subtitles/' + id, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(req)
        })
        .then(ignored => {
            fetch('/subtitles/' + id + '/process', {method: 'PUT'})
            .then(ignored => {
                var proceedButton = document.getElementById("proceed");
                proceedButton.remove();
                getProgress(id)
            });
        });
    });

    function getProgress(id) {
        var socket = new SockJS('/progress');
        var stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/subject/' + id, function (message) {
                const messageJson = JSON.parse(message.body);

                if (messageJson.progress != 100 && messageJson.progress != -1) {
                    document.getElementById('progressBar').value = messageJson.progress;
                    document.getElementById('message').innerText = "Progress: " + messageJson.progress + "\n" + "Current stage: " + messageJson.stage;
                } else if (messageJson.progress == 100) {
                    document.getElementById('progressBar').value = 100;
                    document.getElementById('message').innerText = "Processing is done";

                    var buttonCombined = document.createElement("button");
                    buttonCombined.innerHTML = "download video";
                    buttonCombined.id = "downloadCombinedButton";
                    buttonCombined.className = "btn btn-primary";

                    buttonCombined.onclick = function() {
                        downloadFile(id, 'COMBINED');
                    };

                    var buttonSubtitles = document.createElement("button");
                    buttonSubtitles.innerHTML = "download subtitles";
                    buttonSubtitles.id = "downloadSubtitlesButton";
                    buttonSubtitles.className = "btn btn-primary";

                    buttonSubtitles.onclick = function() {
                        downloadFile(id, 'SUBTITLES');
                    };

                    var container = document.getElementById("proceedContainer");
                    container.appendChild(buttonCombined);
                    container.appendChild(buttonSubtitles);
                } else {
                    document.getElementById('message').innerText = "Error";
                }
            });
        });
    }

    function downloadFile(id, type) {
        var a = document.createElement("a");
        a.href = "/video/" + id + "/" + type + "/download";
        a.download = "video.mp4";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

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
