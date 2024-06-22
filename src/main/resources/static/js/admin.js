let form = document.querySelector('form');
const csrfToken = getCookie('XSRF-TOKEN');


// Fetching data from backend
async function initialiseDatabase(event) {
    event.preventDefault();

    console.log('Attempting to initialise the database...');
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
function addInitialiseDatabaseButton() {
    initialiseDatabaseButton = document.createElement('button');
    initialiseDatabaseButton.textContent = 'Initialise Database';

    initialiseDatabaseButton.addEventListener('click', (event) => {
        initialiseDatabase(event);
    });

    form.appendChild(initialiseDatabaseButton);
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
addInitialiseDatabaseButton();