// Global selectors
const body = document.querySelector('body');


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
    const inputValue = document.querySelector('.search').value;

    async function waitUntilInputEqualsYaoming() {
        await until(inputValue.includes('yaoming'));

        let para = document.createElement('p');
        para.className = 'yao';
        para.textContent = 'hi yaoming!';

        easterEggContainer.appendChild(para);
    }
}


// Initialising the page
addAllEasterEggs();