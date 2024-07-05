// Global selectors
let resultsContainer = document.querySelector('.results-container');


// Important variables list


// Fetching data from backend
async function searchButtonClick(event) {
    event.preventDefault();

    let selectValue = document.querySelector('select').value;
    const searchInputValue = document.querySelector("#search").value;

    if (selectValue == 'phrase') {
        const wordCount = document.querySelector("#word-count-inp").value;

        try {
            const response = await fetch('/findPhrase?' + new URLSearchParams({ phrase: searchInputValue, wordCount: wordCount }), {
                credentials: 'include',
                headers: {
                    'Accept': 'application/json'
                },
            });

            if (!response.ok) {
                const text = await response.text();
                throw Error(text);
            }

            jsonResponse = await response.json();
            addCardsWithData(jsonResponse, selectValue, searchInputValue);

        } catch (error) {
            addConfirmationOrErrorText(`${error.message}`, false);
        }
    } else { // selectValue = 'individual'
        const title = document.querySelector("#search").value;
        const id = document.querySelector("#yt-id-inp");

        if (id) {
            try {
                const response = await fetch('/findTranscriptByVideoId?' + new URLSearchParams({ videoId: id.value }), {
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json'
                    },
                });

                if (!response.ok) {
                    const text = await response.text();
                    throw Error(text);
                }

                jsonResponse = await response.json();
                addCardsWithData(jsonResponse, selectValue, searchInputValue);

            } catch (error) {
                addConfirmationOrErrorText(`${error.message}`, false);
            }
        } else if (title) {
            try {
                const response = await fetch('/findTranscriptByTitle?' + new URLSearchParams({ title: title }), {
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json'
                    },
                });

                if (!response.ok) {
                    const text = await response.text();
                    throw Error(text);
                }

                jsonResponse = await response.json();
                addCardsWithData(jsonResponse, selectValue, searchInputValue);

            } catch (error) {
                addConfirmationOrErrorText(`${error.message}`, false);
                if (error.message.includes('multiple videos')) {
                    // Ask user for specific id
                    addWordCountOrId(false);
                }
            }
        }
    }
}


// Function for parsing the backend data json and calling the card making methods
function addCardsWithData(response, selectValue, searchInputValue) {
    resultsContainerRemoveChildren();

    if (selectValue == 'phrase') {
        const row = addPhraseRow();
        const phrase = searchInputValue;

        const idAndTimestamps = response.idAndTimestamps;
        const context = response.context;

        let i = 0;
        Object.keys(idAndTimestamps).forEach(function(key) {
            const id = key;
            const timestampsInSeconds = idAndTimestamps[key];

            for (const timestamp of timestampsInSeconds) {
                addPhraseCard(row, id, timestamp, context[i]);
                i = ++i;
            }
        });
    } else { // Individual
        const id = response.id;
        const timestampsAndText = response.timestampsAndText;

        addIndividualCard(id, 0, timestampsAndText);
    }
}


// Functions for adding cards depending on the search type
function addPhraseCard(row, id, startingTimestampInSeconds, context) {
    const card = makeCard(id, startingTimestampInSeconds, context, false);

    row.appendChild(card);
}

function addIndividualCard(id, startingTimestampInSeconds, timestampsAndText) {
    const card = makeCard(id, startingTimestampInSeconds, timestampsAndText, true);

    resultsContainer.appendChild(card);
}


// Helper functions
function makeCard(id, startingTimestampInSeconds, text, individualFlag) {
    let col = document.createElement('div')
    col.className = 'col';

    let card = document.createElement('div')
    card.className = 'card';

    let lazyLoadingContainer = document.createElement('div');
    lazyLoadingContainer.className = 'lazy-loading-content image-container';

    // Event listener for loading YT video when clicking on the thumbnail
    lazyLoadingContainer.addEventListener('click', () => {
        // Removes image from card
        card.removeChild(lazyLoadingContainer);

        let videoContainer = document.createElement('div');
        videoContainer.className = 'video-container';

        const iframe = makeIFrame(id, startingTimestampInSeconds, individualFlag);

        // Add the video to the new video container
        videoContainer.appendChild(iframe);

        card.insertBefore(videoContainer, card.firstChild);
    });

    let img = document.createElement('img');
    img.src = 'https://i.ytimg.com/vi/' + id + '/maxresdefault.jpg';

    let playButton = document.createElement('div');
    playButton.className = 'play-button';

    let cardBody = document.createElement('div')
    cardBody.className = 'card-body';

    let cardText = document.createElement('p')
    cardText.className = 'card-text';
    if (individualFlag) {
        addInteractableTextWithTimestamps(cardText, id, text);
    } else {
        cardText.textContent = text;
    }

    col.appendChild(card);
    card.appendChild(lazyLoadingContainer);
    lazyLoadingContainer.appendChild(img);
    lazyLoadingContainer.appendChild(playButton);
    card.appendChild(cardBody);
    cardBody.appendChild(cardText);

    return col;
}

function makeIFrame(id, startingTimestampInSeconds, individualFlag) {
    let iframe = document.createElement('iframe')
    iframe.className = 'card-img-top';
    iframe.src = 'https://www.youtube-nocookie.com/embed/' + id + '?start=' + startingTimestampInSeconds + "&autoplay=1";

    if (individualFlag) {
        iframe.width = 1280;
        iframe.height = 720;
    }

    iframe.allow = 'fullscreen';
    iframe.autoplay = 'allow';
    iframe.referrerPolicy = 'strict-origin-when-cross-origin';

    return iframe;
}

function addPhraseRow() {
    row = document.createElement('div');
    row.className = 'row row-cols-1 row-cols-md-2 g-4';

    resultsContainer.appendChild(row);

    return row;
}

function resultsContainerRemoveChildren() {
    resultsContainer.replaceChildren();
}

// For individual
function addInteractableTextWithTimestamps(cardText, id, textAndTimestamps) {
    // Loops over textAndTimestamps and makes a button per line
    Object.keys(textAndTimestamps).forEach(function(key) {
        const timestamp = key;
        const text = textAndTimestamps[key];

        let btn = document.createElement('button');
        btn.textContent = text;

        // So the text is clickable and loads the video at the timestamp of the text
        btn.addEventListener('click', () => {
            // Keeps the single button highlighted after being clicked
            document.querySelector('.special')?.classList.remove('special');
            btn.classList.add('special');

            const card = document.querySelector('.card');
            // Remove loaded video
            card.removeChild(card.firstChild);

            // Loads new video with the selected timestamp
            card.insertBefore(makeIFrame(id, timestamp, true), card.firstChild);

            // Scroll back up to video after clicking button
            card.scrollIntoView({
                block: 'start'
            });
        });

        cardText.appendChild(btn);
    });
}
