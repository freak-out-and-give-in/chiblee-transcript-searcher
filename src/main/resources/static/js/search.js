// Global selectors
let form = document.querySelector('form');
let explanationContainer = document.querySelector('.explanation-container');


// Important variables list
const phraseExplanation = 'This will list every time Chiblee has said the entered phrase';
const phrasePlaceholder = 'i am the joker';
const phraseWordCountExplanation = 'Word count each side';
const phraseWordCountValue = '15';
const phraseWordCountPlaceholder = '15';
const phraseImageTooltipText = "For example: entering the phrase 'my trump impression', and setting '4' words each side\n" +
     "will return: 'thank you that was my trump impression can I could I'";

const individualExplanation = "This will list the transcript of the video with the entered title";
const individualPlaceholder = "HE CAN'T STOP HITTING NEW PBS (SM64 Part 5)";
const individualIdExplanation = "There are multiple videos with this title, please also enter the video's id";
const individualIdValue = ''
const individualIdPlaceholder = 'QXvsq87UzOg';
const individualImageTooltipText = "For example: the video 'https://www.youtube.com/watch?v=6MA39Rp4B5Y'\n" +
    "has the id '6MA39Rp4B5Y'";

const questionMarkImageSrc = '/static/images/question-mark-icon.png';


// Select options
function addPhraseOptions() {
    resetAndAddFormExplanationAndSelect(phraseExplanation);

    addInput(phrasePlaceholder);
    addSearchButton();
    addWordCountOrIdExplanationAndInput(true, phraseWordCountExplanation, phraseWordCountPlaceholder, phraseWordCountValue, phraseImageTooltipText);
}

function addIndividualOptions() {
    resetAndAddFormExplanationAndSelect(individualExplanation);

    addInput(individualPlaceholder);
    addSearchButton();

    // Only if the title isn't unique (duplicateTitleFlag):
    addWordCountOrIdExplanationAndInput(false, individualIdExplanation, individualIdPlaceholder, individualIdValue, individualImageTooltipText);
}


// Combined functions for form
function resetAndAddFormExplanationAndSelect(explanation) {
    resetExplanationAndForm();

    addExplainingParagraph(explanation);
    addSelectOptions(explanation);
}

function addWordCountOrIdExplanationAndInput(wordCountFlag, explanation, placeholder, value, imageTooltipText) {
    addExplanationForWordCountOrId(wordCountFlag, explanation, imageTooltipText);
    addWordCountOrIdInput(wordCountFlag, placeholder, value);
}


// Helper functions used directly for form
function addInput(placeholderText) {
    input = document.createElement('input');
    input.className = 'search';
    input.placeholder = placeholderText;
    input.required = true;

    form.appendChild(input);
}

function addSearchButton() {
    searchButton = document.createElement('button');
    searchButton.className = 'btn btn-primary btn-sm';
    searchButton.textContent = 'Submit';

    // If submit is sent
    searchButton.addEventListener('click', (event, inputValue) => {
        searchButtonClick(event);
    });

    form.appendChild(searchButton);
}


// Helper functions used to be combined for form
function resetExplanationAndForm() {
    explanationContainer.replaceChildren();
    form.replaceChildren();
}

function addExplainingParagraph(explanation) {
    para = document.createElement('p');
    para.className = 'explanation';
    para.textContent = explanation;

    explanationContainer.appendChild(para);
}

function addSelectOptions(explanation) {
    select = document.createElement('select');
    select.className = 'form-select form-select-lg mb-3';
    select.setAttribute('aria-label', 'Large select example');

    optionPhrase = document.createElement('option');
    optionPhrase.value = 'phrase';
    optionPhrase.setAttribute('name', 'phrase');
    optionPhrase.textContent = 'Phrase';

    optionIndividual = document.createElement('option');
    optionIndividual.value = 'individual';
    optionIndividual.setAttribute('name', 'individual');
    optionIndividual.textContent = 'Individual';
    if (explanation.includes(individualExplanation)) {
        optionIndividual.selected = true;
    }

    select.appendChild(optionPhrase);
    select.appendChild(optionIndividual);

    select.addEventListener('change', () => {
        selectValue = document.querySelector('select').value;

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

function addExplanationForWordCountOrId(wordCountFlag, explanation, imageTooltipText) {
    para = document.createElement('p');
    para.textContent = explanation;

    if (wordCountFlag) {
        para.className = 'word-count-p';
    } else {
        para.className = 'yt-id-p';
    }

    explanationContainer.appendChild(para);
    para.appendChild(addImageTooltip(imageTooltipText));
}

function addWordCountOrIdInput(wordCountFlag, placeholder, value) {
    input = document.createElement('input');
    input.placeholder = placeholder;
    input.value = value;

    if (wordCountFlag) {
        input.className = 'word-count-inp';
        input.required = true;
        input.type = 'number';
        input.min = 0;
        input.max = 30;
    } else {
        input.className = 'yt-id-inp';
        input.required = true;
        input.setAttribute('minlength', 11);
        input.setAttribute('maxlength', 11);
    }

    form.appendChild(input);
}

function addImageTooltip(text) {
    img = document.createElement('img');
    img.src = questionMarkImageSrc;
    img.title = text;
    img.alt = img.title;
    img.width = '16';
    img.height = '16'

    return img;
}


// Initialising the page
addPhraseOptions();