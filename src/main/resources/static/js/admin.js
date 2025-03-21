const csrfToken = getCookie('XSRF-TOKEN');

const buttonSelector = ' > form';
const deleteTranscriptsAndArchiveClassName = '.box.dta';
const downloadTxtTranscriptsClassName = '.box.dt';
const createTranscriptDatabaseClassName = '.box.tdb';
const createInvertedIndexDatabaseClassName = '.box.iidb';
const downloadTranscriptsAndCreateDatabasesClassName = '.box.all';

const deleteTranscriptsAndArchiveMessage = 'Delete transcripts';
const downloadTxtTranscriptsMessage = 'Download transcripts';
const createTranscriptDatabaseMessage = 'Create the transcript database';
const createInvertedIndexDatabaseMessage = 'Create the inverted index database';
const downloadTranscriptsAndCreateDatabasesMessage = 'Download & create all';


// Fetching data from backend
async function deleteTxtTranscriptsAndArchive(event) {
    event.preventDefault();

    const response = await fetch('/admin/deleteTxtTranscriptsAndArchive', {
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

async function downloadTxtTranscripts(event) {
    event.preventDefault();

    const response = await fetch('/admin/downloadTxtTranscripts', {
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

async function createTranscriptDatabase(event) {
    event.preventDefault();

    const response = await fetch('/admin/createTranscriptDB', {
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

async function createInvertedIndexDatabase(event) {
    event.preventDefault();

    const response = await fetch('/admin/createInvertedIndexDB', {
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

async function downloadTranscriptsAndCreateDatabases(event) {
    event.preventDefault();

    const response = await fetch('/admin/downloadTranscriptsAndCreateDB', {
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
    let form;
    switch (fetchEvent) {
        case deleteTxtTranscriptsAndArchive:
            form = document.querySelector(deleteTranscriptsAndArchiveClassName + buttonSelector);
            break;
        case downloadTxtTranscripts:
            form = document.querySelector(downloadTxtTranscriptsClassName + buttonSelector);
            break;
        case createTranscriptDatabase:
            form = document.querySelector(createTranscriptDatabaseClassName + buttonSelector);
            break;
        case createInvertedIndexDatabase:
            form = document.querySelector(createInvertedIndexDatabaseClassName + buttonSelector);
            break;
        case downloadTranscriptsAndCreateDatabases:
            form = document.querySelector(downloadTranscriptsAndCreateDatabasesClassName + buttonSelector);
            break;
    }

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
addButton(deleteTranscriptsAndArchiveMessage, deleteTxtTranscriptsAndArchive);
addButton(downloadTxtTranscriptsMessage, downloadTxtTranscripts);
addButton(createTranscriptDatabaseMessage, createTranscriptDatabase);
addButton(createInvertedIndexDatabaseMessage, createInvertedIndexDatabase);
addButton(downloadTranscriptsAndCreateDatabasesMessage, downloadTranscriptsAndCreateDatabases);