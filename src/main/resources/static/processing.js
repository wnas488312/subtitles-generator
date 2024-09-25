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

    const fontNameDropDown = document.getElementById('fontName');
    const bottomMarginInput = document.getElementById('bottomMargin');
    const fontSizeInput = document.getElementById('fontSize');
    const fontColorInput = document.getElementById('fontColor');

    const req = {
        bottomMargin: parseInt(bottomMarginInput.value),
        fontName: fontNameDropDown.value.replace('CUSTOM_', ''),
        textChunks: textChunks,
        fontSize: parseInt(fontSizeInput.value),
        fontColor: hexToRgb(fontColorInput.value),
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

function hexToRgb(hex) {
    hex = hex.replace(/^#/, '');

    let bigint = parseInt(hex, 16);
    let rValue = (bigint >> 16) & 255;
    let gValue = (bigint >> 8) & 255;
    let bValue = bigint & 255;

    return { 
        r: rValue, 
        g: gValue, 
        b: bValue 
    };
}