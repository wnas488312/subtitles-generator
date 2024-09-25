const fontSizeInput = document.getElementById('fontSize');
const fontNameDropDown = document.getElementById('fontName');
const bottomMarginInput = document.getElementById('bottomMargin');
const fontColorInput = document.getElementById('fontColor');

function getDefaultProperties() {
    fetch('/properties/default', { method: 'GET' })
        .then(response => response.json())
        .then(data => populatePropertiesForm(data));
}

function populatePropertiesForm(properties) {
    const dropdown = document.getElementById('fontName');
    properties.fontNames.forEach(fontName => {
        const fontNameNoSpace = fontName.replace(/\s+/g, '');
        const opt = document.createElement('option');
        opt.value = fontNameNoSpace;
        opt.text = fontName.replace('CUSTOM_', '');
        dropdown.appendChild(opt);
    });

    fontNameDropDown.value = properties.defaultFontName;
    fontSizeInput.value = properties.fontSize;
    bottomMarginInput.value = properties.bottomMargin;
    fontColorInput.value = rgbToHex(properties.fontColor.r, properties.fontColor.g, properties.fontColor.b);

    fontSizeInput.addEventListener('change', () => updateProperties());
    fontNameDropDown.addEventListener('change', () => updateProperties());
    fontColorInput.addEventListener('change', () => updateProperties());
    bottomMarginInput.addEventListener('change', () => updateProperties());

    updateProperties();
}

function updateProperties() {
    const fontNameValue = fontNameDropDown.value;
    if (fontNameValue.indexOf('CUSTOM_') !== -1) {
        const fontName = fontNameValue.replace('CUSTOM_', '');
        const fontUrl = `http://localhost:8080/fonts/${fontName}`;

        const style = document.createElement('style');
        style.innerHTML = `
            @font-face {
                font-family: '${fontName}';
                src: url('${fontUrl}') format('truetype');
            }
            `;
        document.head.appendChild(style);
        document.getElementById('textOverlay').style.fontFamily = fontName;
    }

    const fontSize = parseInt(fontSizeInput.value);

    const ratio = videoWidth == undefined? 1: 600 / videoWidth;

    const fontValue = fontSize * ratio + "px " + fontName;

    let textOverlay = document.getElementById('textOverlay');
    textOverlay.style.font = fontValue;
    textOverlay.style.color = fontColorInput.value;
    textOverlay.style.top = (-((fontSize * 1.5) + parseInt(bottomMarginInput.value)) * ratio) + "px";
}

function rgbToHex(r, g, b) {
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1).toUpperCase();
}