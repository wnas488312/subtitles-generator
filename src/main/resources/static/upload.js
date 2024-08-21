function connectWebSocket(id, fileName) {
    const url = `ws://localhost:8080/video/original/${id}?fileName=${encodeURIComponent(fileName)}`;
    socket = new WebSocket(url);
    socket.binaryType = 'arraybuffer';

    socket.onopen = function () {
        console.log("WebSocket connection opened");
    };

    socket.onmessage = function (event) {
        console.log("Message from server: ", event.data);
    };

    socket.onclose = function () {
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

        reader.onload = function (event) {
            if (event.target.readyState === FileReader.DONE) {
                socket.send(event.target.result);
                offset += chunkSize;

                const progress = Math.min((offset / file.size) * 100, 100);

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