// Global selectors
const body = document.querySelector('body');
const searchInput = document.querySelector('#search');
const searchButton = document.querySelector('.search-button');


// Super function that adds all the functions together
function addAllEasterEggs() {
    let easterEggContainer = addEasterEggContainer();

    addYaomingEasterEgg(easterEggContainer);
}


// Helper function for initialising
function addEasterEggContainer() {
    let easterEggContainer = document.createElement('div');
    easterEggContainer.className = 'e-e';

    return easterEggContainer;
}


// Functions for adding specific easter eggs
function addYaomingEasterEgg(easterEggContainer) {
    searchButton.addEventListener('click', () => {
        const searchText = searchInput.value.toLowerCase();

        if (searchText.includes('yao ming') || searchText.includes('yaoming')) {
            const videoContainer = addVideoContainer();

            const video = document.createElement('video');
            video.controls = true;

            const source = document.createElement('source');
            source.setAttribute('src', '/video/yaoming-video.mp4');
            source.setAttribute('type', 'video/mp4');

            video.appendChild(source);

            // The video is removed once it has ended
            video.addEventListener('ended', () => {
                body.removeChild(videoContainer);
            });

            videoContainer.appendChild(video);
        }
    });

    function addVideoContainer() {
        const videoContainer = document.createElement('div');
        videoContainer.className = 'e-e-video-container';

        body.appendChild(videoContainer);

        return videoContainer;
    }
}


// Initialising the page
addAllEasterEggs();