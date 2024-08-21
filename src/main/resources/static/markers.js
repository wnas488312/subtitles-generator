function createMarkers() {
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
}