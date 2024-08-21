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

                buttonCombined.onclick = function () {
                    downloadFile(id, 'COMBINED');
                };

                var buttonSubtitles = document.createElement("button");
                buttonSubtitles.innerHTML = "download subtitles";
                buttonSubtitles.id = "downloadSubtitlesButton";
                buttonSubtitles.className = "btn btn-primary";

                buttonSubtitles.onclick = function () {
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