// Global selectors
let resultsContainer = document.querySelector('.results-container');


// Important variables list


// Fetching data from backend
async function searchButtonClick(event) {
    event.preventDefault();

    let selectValue = document.querySelector('select').value;
    const inputValue = document.querySelector(".search").value;

    if (selectValue == 'phrase') {
        const wordCount = document.querySelector(".word-count-inp").value;

        const response = await fetch('/findThisPhrase?' + new URLSearchParams({ phrase: inputValue, wordCount: wordCount }), {
                credentials: 'include',
                headers: {
                    'Accept': 'application/json'
                },
            })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error: ${response.status}`);
                }

            return response;
            })
            .then((result) => {
                return result.json();
            })
            .catch((error) => {
                console.log(`Could not fetch verse: ${error}`);
            });
    } else { // Individual
        const id = document.querySelector(".yt-id-inp").value;

        const response = await fetch('/findThisTranscript?' + new URLSearchParams({ id: id }), {
                credentials: 'include',
                headers: {
                    'Accept': 'application/json'
                },
            })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error: ${response.status}`);
                }

            return response;
            })
            .then((result) => {
                return result.json();
            })
            .catch((error) => {
                console.log(`Could not fetch verse: ${error}`);
            });
    }


    addCardsWithData(response, selectValue);
}


// Function for parsing the backend data json and calling the card making methods
function addCardsWithData(response, selectValue) {
    const jsonObject = JSON.stringify(response);
    resultsContainerRemoveChildren();

    if (selectValue == 'phrase') {
        const row = addPhraseRow();
    }

    for (let [id, timestampsAndText] of Object.entries(jsonObject)) {
        JSON.parse(JSON.stringify(timestampsAndText), function (timestamp, phrase) {
            if (timestamp && selectValue == 'phrase') {
                addPhraseCard(row, id, timestamp, phrase);
            } else if (timestamp) {
                addIndividualCard(id, timestamp, phrase);
            }
        });
    }
}


// Functions for adding cards depending on the search type
function addPhraseCard(row, id, timestampInSeconds, text) {
    const card = makeCard(id, timestampInSeconds, text, false);

    row.appendChild(card);
}

function addIndividualCard(id, timestampInSeconds, text) {
    const card = makeCard(id, timestampInSeconds, text, true);

    resultsContainer.appendChild(card);
}


// Helper functions
function makeCard(id, timestampInSeconds, text, individualFlag) {
    col = document.createElement('div')
    col.className = 'col';

    card = document.createElement('div')
    card.className = 'card';

    iframe = document.createElement('iframe')
    iframe.className = 'card-img-top';
    iframe.src = 'https://www.youtube-nocookie.com/embed/' + id + '?;start=' + timestampInSeconds;
    if (individualFlag) {
        iframe.width = 1280;
        iframe.height = 720;
    }
    iframe.allow = 'fullscreen';
    iframe.referrerPolicy = 'strict-origin-when-cross-origin';

    cardBody = document.createElement('div')
    cardBody.className = 'card-body';

    cardText = document.createElement('p')
    cardText.className = 'card-text';
    // text received from backend should be here:
    cardText.textContent = text;

    col.appendChild(card);
    card.appendChild(iframe);
    card.appendChild(cardBody);
    cardBody.appendChild(cardText);

    return col;
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