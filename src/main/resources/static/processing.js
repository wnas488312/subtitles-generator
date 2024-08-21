function startProcessing() {
    document.getElementById('progressBar').value = 0;
    document.getElementById('message').innerText = "Preparing for subtitles processing...";

    const textChunks = []
    for (let i = 0; i < storedResponse.length; i++) {
        const startFrame = sliderValues[i] / 100 * videoPlayer.duration * 24;
        let endFrame = sliderValues[i + 1] / 100 * videoPlayer.duration * 24;

        if (i == storedResponse.length - 1) {
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
            fetch('/subtitles/' + id + '/process', { method: 'PUT' })
                .then(ignored => {
                    var proceedButton = document.getElementById("proceed");
                    proceedButton.remove();
                    getProgress(id)
                });
        });
}