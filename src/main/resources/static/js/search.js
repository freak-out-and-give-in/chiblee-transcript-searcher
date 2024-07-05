// Global selectors
let inputContainer = document.querySelector('.input-container');
let searchContainer = document.querySelector('.search-container');
let extraInputContainer = document.querySelector('.extra-input-container');
let errorConfirmationContainer = document.querySelector('.error-confirmation-container');


// Important variables list
const phraseExplanation = 'Enter a phrase';
const phrasePlaceholder = 'im the joker baby';
const phraseWordCountExplanation = 'Word count';
const phraseWordCountValue = '20';
const phraseWordCountPlaceholder = '20';
const wordCountMinimum = 0;
const wordCountMaximum = 100;

const individualExplanation = "Enter the title of a youtube video from @ChibleeVODs";
const individualPlaceholder = "HE CAN'T STOP HITTING NEW PBS (SM64 Part 5)";
const individualIdExplanation = "There are multiple videos with this title, or the title does not exist. Please also enter the video's id";
const individualIdValue = ''
const individualIdPlaceholder = 'mKhnheKTp98';
const individualImageTooltipText = "e.g. this video has the id '6MA39Rp4B5Y': 'https://www.youtube.com/watch?v=6MA39Rp4B5Y'";

const questionMarkImageSrc = '/images/question-mark-icon.png';
const confirmationMessage = 'Successfully submitted';


// Select options
function addPhraseOptions() {
    removeInputContainerGrandChildren();

    addExplanationForSearch(searchContainer, phraseExplanation);
    addSearchForm(false);

    addWordCountOrId(true);
}

function addIndividualOptions() {
    removeInputContainerGrandChildren();

    addExplanationForSearch(searchContainer, individualExplanation);
    addSearchForm(true);
}


// Combined functions
// Adds the search part of the form to the search-container
function addSearchForm(individualFlag) {
    let form = document.createElement('form');
    form.id = 'form';
    searchContainer.appendChild(form);

    addSelectOptions(form, individualFlag);

    if (individualFlag) {
        addSearchInput(form, individualPlaceholder, individualFlag);
    } else {
        addSearchInput(form, phrasePlaceholder, individualFlag);
    }

    addSearchButton(form);
}

// Removes everything other than the starting divs
function removeInputContainerGrandChildren() {
    searchContainer.replaceChildren();
    extraInputContainer.replaceChildren();
    errorConfirmationContainer.replaceChildren();
}

function addWordCountOrId(wordCountFlag) {
    extraInputContainer.replaceChildren();

    addExplanationForWordCountOrId(wordCountFlag);
    addWordCountOrIdInput(wordCountFlag);
}


// Helper functions for combined functions
function addSelectOptions(form, individualFlag) {
    let select = document.createElement('select');
    select.id = 'select';
    select.className = 'form-select form-select-lg mb-3';
    select.setAttribute('aria-label', 'Pick a search type: phrase or individual');

    let optionPhrase = document.createElement('option');
    optionPhrase.value = 'phrase';
    optionPhrase.setAttribute('name', 'phrase');
    optionPhrase.textContent = 'Phrase';

    let optionIndividual = document.createElement('option');
    optionIndividual.value = 'individual';
    optionIndividual.setAttribute('name', 'individual');
    optionIndividual.textContent = 'Individual';
    if (individualFlag) {
        optionIndividual.selected = true;
    }

    select.appendChild(optionPhrase);
    select.appendChild(optionIndividual);

    select.addEventListener('change', () => {
        selectValue = select.value;

        switch (selectValue) {
            case 'phrase': {
                addPhraseOptions();
                break;
            }

            case 'individual': {
                addIndividualOptions();
                break;
            }
        }
    });

    form.appendChild(select);
}

function addSearchInput(form, placeholderText, individualFlag) {
    let input = document.createElement('input');
    input.id = 'search';
    input.placeholder = placeholderText;
    input.required = true;

    if (individualFlag) {
        input.ariaLabel = "Search for a video's transcript by entering a title";
    } else {
        input.ariaLabel = 'Search for the instances of a phrase by entering the wanted phrase';
    }

    form.appendChild(input);
}

function addSearchButton(form) {
    let searchButton = document.createElement('button');
    searchButton.className = 'search-button btn btn-primary btn-sm';
    searchButton.textContent = 'Submit';

    // If submit is sent
    searchButton.addEventListener('click', (event) => {
        // Adds confirmation text that the form has been submitted
        addConfirmationOrErrorText(confirmationMessage, true);
        searchButtonClick(event);
    });

    form.appendChild(searchButton);
}


// Functions for search-container
// Adds paragraph to explain the search type
function addExplanationForSearch(container, explanation) {
    let para = document.createElement('p');
    para.className = 'explanation';
    para.textContent = explanation;

    container.appendChild(para);
}


// Functions for extra-input-container
// Adds paragraph to word-count-or-id
function addExplanationForWordCountOrId(wordCountFlag) {
    let para = document.createElement('p');

    if (wordCountFlag) {
        para.className = 'word-count-p';
        para.textContent = phraseWordCountExplanation;
    } else {
        para.className = 'yt-id-p';
        para.textContent = individualIdExplanation;
        para.appendChild(getImageTooltip(individualImageTooltipText));
    }

    extraInputContainer.appendChild(para);
}

// Adds input to word-count-or-id, and linked up with the form
function addWordCountOrIdInput(wordCountFlag) {
    let input = document.createElement('input');
    // The form attribute equals the form's id
    input.form = 'form';

    if (wordCountFlag) {
        input.id = 'word-count-inp';
        input.placeholder = phraseWordCountPlaceholder;
        input.value = phraseWordCountValue;
        input.required = true;
        input.ariaLabel = 'The amount of words that will show as text beneath each video result';
        input.type = 'number';
        input.min = wordCountMinimum;
        input.max = wordCountMaximum;
    } else {
        input.id = 'yt-id-inp';
        input.placeholder = individualIdPlaceholder;
        input.value = individualIdValue;
        input.required = true;
        input.ariaLabel = "There are multiple videos with this title, or the title does not exist. Please also enter the video's id";
        input.setAttribute('minlength', 11);
        input.setAttribute('maxlength', 11);

        // So the confirmation message doesn't show when nothing has been submitted and the id is needed
        removeConfirmationOrErrorText();
    }

    extraInputContainer.appendChild(input);
}


// Functions for error-confirmation-container
// Adds a box that displays a confirmation or error message
function addConfirmationOrErrorText(message, confirmationFlag) {
    removeConfirmationOrErrorText();

    const confirmationOrErrorMessage = message;

    let para = document.createElement('p');
    para.textContent = confirmationOrErrorMessage;

    if (confirmationFlag) {
        para.className = 'confirmation-text';
    } else {
        para.className = 'error-text';
    }

    errorConfirmationContainer.appendChild(para);
}

function removeConfirmationOrErrorText() {
    errorConfirmationContainer.replaceChildren();
}


// Helper functions for the functions
function getImageTooltip(text) {
    let img = document.createElement('img');
    img.src = questionMarkImageSrc;
    img.title = text;
    img.alt = img.title;
    img.width = '16';
    img.height = '16'

    return img;
}


// Initialising the page
addPhraseOptions();