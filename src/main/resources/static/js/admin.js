let form = document.querySelector('form');
const csrfToken = getCookie('XSRF-TOKEN');


// Fetching data from backend
async function initialiseTranscripts(event) {
    event.preventDefault();

    const response = await fetch('/admin/initTranscripts', {
            credentials: 'include',
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': csrfToken
            }
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }

            return response;
        })
        .then((result) => {
            return result;
        })
        .catch((error) => {
            console.log(`Could not fetch verse: ${error}`);
        });
}

async function downloadTranscripts(event) {
    event.preventDefault();

    const response = await fetch('/admin/downloadTranscripts', {
            credentials: 'include',
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': csrfToken
            }
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }

            return response;
        })
        .then((result) => {
            return result;
        })
        .catch((error) => {
            console.log(`Could not fetch verse: ${error}`);
        });
}

async function initialiseInvertedIndexes(event) {
    event.preventDefault();

    const response = await fetch('/admin/initDB', {
            credentials: 'include',
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': csrfToken
            }
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }

            return response;
        })
        .then((result) => {
            return result;
        })
        .catch((error) => {
            console.log(`Could not fetch verse: ${error}`);
        });
}


// Helper functions
function addButton(textContent, fetchEvent) {
    button = document.createElement('button');
    button.textContent = textContent;

    button.addEventListener('click', (event) => {
        fetchEvent(event);
    });

    form.appendChild(button);
}

function getCookie(name) {
    if (!document.cookie) {
        return null;
    }

    const xsrfCookies = document.cookie.split(';')
        .map(c => c.trim())
        .filter(c => c.startsWith(name + '='));

    if (xsrfCookies.length === 0) {
        return null;
    }

    return decodeURIComponent(xsrfCookies[0].split('=')[1]);
}


//Initialising the page
addButton('Download transcripts', downloadTranscripts);
addButton('Initialise transcripts', initialiseTranscripts);
addButton('Initialise inverted index', initialiseInvertedIndexes);