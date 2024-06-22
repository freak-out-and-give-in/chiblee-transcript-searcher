// Global selectors
const root = document.querySelector(':root');
titleButtons = document.querySelectorAll('.title-container button');


// Function to show attribution text
function addShowAttributionTextOnClick() {
    const creditButton = document.querySelector('.attribution-container button');

    creditButton.addEventListener('click', () => {
        creditButton.replaceChildren();

        let paraArtFart = document.createElement('p');
        paraArtFart.textContent = 'Thanks to ArtFart for letting me use their art for the website\'s icon!';

        creditButton.appendChild(paraArtFart);
    });
}


// Function to give the title buttons the functionality of changing colors
function addFunctionalityToTitleButtons() {
    for (const button of titleButtons) {
        button.addEventListener('click', () => {
            setColorScheme(button.getAttribute('data-color-scheme'));
        });
    }
}


// Color scheme functions which change the colors
function setColorScheme(color) {
    const containerColor = makeMap('--container-color', '#EDDEA4');
    const titleContainerColor = makeMap('--title-container-color', '#08A4BD');
    const resultsContainerColor = makeMap('--results-search-container-color', '#C179B9');

    const titleColor = makeMap('--title-color', 'black');
    const vodHyperlinkColor = makeMap('--vod-hyperlink-color', 'black');
    const attributionTextColor = makeMap('--attribution-text-color', 'black');

    const explanationParaColor = makeMap('--explanation-para-color', 'black');
    const inputBackgroundColor = makeMap('--input-background-color', '#efefef');
    const inputTextColor = makeMap('--input-text-color', 'black');
    const buttonBackgroundColor = makeMap('--button-background-color', '#6A7096');
    const buttonTextColor = makeMap('--button-text-color', '#efefef');
    const dividerColor = makeMap('--divider-color', '#3F4045');

    const cardBackgroundColor = makeMap('--card-background-color', '#efefef');
    const cardBorderColor = makeMap('--card-border-color', 'rgb(196, 230, 243)');
    const cardBorderWidth = makeMap('--card-border-width', '2px');

    switch (color) {
        case 'dark': {
            setVariableValue(containerColor, '#191510');
            setVariableValue(titleContainerColor, '#171911');
            setVariableValue(resultsContainerColor, '#333428');

            setVariableValue(titleColor, '#28282B');
            setVariableValue(vodHyperlinkColor, '#28282B');
            setVariableValue(attributionTextColor, '#28282B');

            setVariableValue(inputBackgroundColor, 'grey');
            setVariableValue(inputTextColor, '#28282B');
            setVariableValue(buttonBackgroundColor, 'grey');
            setVariableValue(buttonTextColor, '#28282B');
            setVariableValue(dividerColor, '#28282B');
            break;
        };

        case 'light': {
            setVariableValue(containerColor, '#cdb4db');
            setVariableValue(titleContainerColor, '#ffafcc');
            setVariableValue(resultsContainerColor, '#a2d2ff');

            setVariableValue(inputBackgroundColor, '#E0E0E0');
            setVariableValue(dividerColor, '#52545A');
            break;
        };

        case 'murder': {
            setVariableValue(containerColor, '#0b090a');
            setVariableValue(titleContainerColor, '#370707');
            setVariableValue(resultsContainerColor, '#660708');

            setVariableValue(inputBackgroundColor, '#b1a7a6');
            setVariableValue(inputTextColor, '#161a1d');
            setVariableValue(buttonBackgroundColor, '#b1a7a6');
            setVariableValue(buttonTextColor, '#161a1d');
            break;
        };
    }

    const arrayOfMaps = [];
    arrayOfMaps.push(containerColor, titleContainerColor, resultsContainerColor, titleColor, titleColor, vodHyperlinkColor, attributionTextColor, explanationParaColor,
    inputBackgroundColor, inputTextColor, buttonBackgroundColor, buttonTextColor, dividerColor, cardBackgroundColor, cardBorderColor, cardBorderWidth)
    arrayOfMaps.forEach((map) => setRootPropertyValue(map));
}

function setVariableValue(element, value) {
    element.set(element.keys().next().value, value);
}

function setRootPropertyValue(map) {
    const key = map.keys().next().value;

    root.style.setProperty(key, map.get(key));
}

function makeMap(property, value) {
    return new Map([[property, value]]);
}


// Initialising the page
addFunctionalityToTitleButtons();
addShowAttributionTextOnClick();
setColorScheme('default');