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
        opt.text = fontName;
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
    const fontName = fontNameDropDown.value;
    const fontSize = parseInt(fontSizeInput.value);

    const ratio = videoWidth == undefined? 1: 600 / videoWidth;
    console.log("Ratio: " + ratio);

    const fontValue = fontSize * ratio + "px " + fontName;

    let textOverlay = document.getElementById('textOverlay');
    textOverlay.style.font = fontValue;
    textOverlay.style.color = fontColorInput.value;
    textOverlay.style.top = (-((fontSize * 1.5) + parseInt(bottomMarginInput.value)) * ratio) + "px";
}

function rgbToHex(r, g, b) {
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1).toUpperCase();
}