function getDefaultProperties() {
    let properties;
    fetch('/properties/default', { method: 'GET' })
        .then(response => response.json())
        .then(data => populatePropertiesForm(data));
}

function populatePropertiesForm(properties) {
    console.log("default Font Name: " + properties.defaultFontName)

    console.log("Font Name 1241241: " + properties.fontNames)

    const dropdown = document.getElementById('fontName');
    properties.fontNames.forEach(fontName => {
        console.log("Font Name: " + fontName)
        const fontNameNoSpace = fontName.replace(/\s+/g, '');
        const opt = document.createElement('option');
        opt.value = fontNameNoSpace;
        opt.text = fontName;
        dropdown.appendChild(opt);
    });

    document.getElementById('fontName').value = properties.defaultFontName;
    document.getElementById('fontSize').value = properties.fontSize;
    document.getElementById('bottomMargin').value = properties.bottomMargin;
    document.getElementById('fontColor').value = rgbToHex(properties.fontColor.r, properties.fontColor.g, properties.fontColor.b);
}

function rgbToHex(r, g, b) {
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1).toUpperCase();
}