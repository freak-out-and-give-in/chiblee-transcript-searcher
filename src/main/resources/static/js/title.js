// Global selectors
const root = document.querySelector(':root');
titleButtons = document.querySelectorAll('.title-container button');


// Function to show attribution text
function addShowAttributionGithubOrFeedbackText() {
    const attributeContainer = document.querySelector('.attribution-container');
    const creditButtons = document.querySelectorAll('.attribution-container button');

    for (const btn of creditButtons) {
        btn.addEventListener('click', () => {
            const oldPara = document.querySelector('.attribution-container p');
            attributeContainer.removeChild(oldPara);

            const newPara = document.createElement('p');

            switch (btn.id) {
                case 'attribution': {
                    newPara.textContent = "Thanks to ArtFart, who let me use their art as the website's icon";
                    const faviconImg = document.createElement('img');
                    faviconImg.src = '/images/favicon.ico';
                    newPara.appendChild(faviconImg);

                    break;
                }
                case 'github': {
                    let githubLink = document.createElement('a');
                    githubLink.textContent = 'freak-out-and-give-in/\nchiblee-transcript-searcher';
                    githubLink.href = 'https://github.com/freak-out-and-give-in/chiblee-\ntranscript-searcher';
                    githubLink.target = '_blank';
                    newPara.appendChild(githubLink);

                    break;
                }
                case 'feedback': {
                    newPara.textContent = 'Feel free to message me with\n feedback or suggestions!';

                    break;
                }

            }

            attributeContainer.appendChild(newPara);
        });
    }
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
    const resultsContainerColor = makeMap('--main-color', '#C179B9');

    const titleColor = makeMap('--title-color', 'black');
    const titleFontFamily = makeMap('--title-font-family', 'OldChibleeFont');
    const vodHyperlinkColor = makeMap('--vod-hyperlink-color', 'black');
    const attributionTextColor = makeMap('--attribution-text-color', 'black');

    const inputParaTextColor = makeMap('--input-para-text-color', 'black');
    const inputBackgroundColor = makeMap('--input-background-color', '#efefef');
    const inputTextColor = makeMap('--input-text-color', 'black');
    const inputFontFamily = makeMap('--input-font-family', 'Georgia, serif');

    const buttonBackgroundColor = makeMap('--button-background-color', '#3A6360');
    const buttonTextColor = makeMap('--button-text-color', '#efefef');

    const dividerColor = makeMap('--divider-color', '#3F4045');
    const confirmationTextColor = makeMap('--confirmation-text-color', 'green');
    const errorTextColor = makeMap('--error-text-color', 'maroon');

    const cardBackgroundColor = makeMap('--card-background-color', '#efefef');
    const cardBorderColor = makeMap('--card-border-color', 'rgb(196, 230, 243)');
    const cardBorderWidth = makeMap('--card-border-width', '2px');

    switch (color) {
        case 'dark': {
            setVariableValue(containerColor, '#191510');
            setVariableValue(titleContainerColor, '#171911');
            setVariableValue(resultsContainerColor, '#333428');

            setVariableValue(titleColor, '#45454A');
            setVariableValue(vodHyperlinkColor, '#45454A');
            setVariableValue(attributionTextColor, '#45454A');

            setVariableValue(inputBackgroundColor, 'grey');
            setVariableValue(inputTextColor, '#28282B');

            setVariableValue(buttonBackgroundColor, 'grey');
            setVariableValue(buttonTextColor, '#28282B');
            setVariableValue(dividerColor, '#28282B');
            break;
        };

        case 'light': {
            setVariableValue(containerColor, '#FFFFFF');
            setVariableValue(titleContainerColor, '#5BCEFA');
            setVariableValue(resultsContainerColor, '#F5A9B8');

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
            setVariableValue(inputFontFamily, 'Creepster');

            setVariableValue(buttonBackgroundColor, '#b1a7a6');
            setVariableValue(buttonTextColor, '#161a1d');
            setVariableValue(errorTextColor, '#161a1d');
            break;
        };
    }

    const arrayOfMaps = [];
    arrayOfMaps.push
    (
    containerColor, titleContainerColor, resultsContainerColor,
    titleColor, titleFontFamily, titleColor, vodHyperlinkColor, attributionTextColor,
    inputParaTextColor, inputBackgroundColor, inputTextColor, inputFontFamily,
    buttonBackgroundColor, buttonTextColor,
    dividerColor, confirmationTextColor, errorTextColor,
    cardBackgroundColor, cardBorderColor, cardBorderWidth
    )

    arrayOfMaps.forEach((map) => setRootPropertyValue(map));
}

// Helper functions for changing color
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
addShowAttributionGithubOrFeedbackText();
setColorScheme('default');