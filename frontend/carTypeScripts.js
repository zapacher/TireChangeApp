document.addEventListener('DOMContentLoaded', () => {
    
    // Function to make the initial POST request
    function getLondon() {
        const london = document.getElementById('london');
        fetch('http://localhost:8080/tire_change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: '"LONDON"'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            // displayElement.textContent = `Response from POST: ${JSON.stringify(data)}`;
            london.textContent = 'london1111';
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

    function getManchester() {
        const manchester = document.getElementById('manchester');
        fetch('http://localhost:8080/tire_change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: '"MANCHESTER"'
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    const errorMessage = errorData.message || `Error: ${response.status}`;
                    manchester.textContent = errorData.message;
                });
                
                throw new Error('Network response was not ok ' + response.statusText);
            }
        })
        .then(data => {
            console.log(data)
            // displayElement.textContent = `Response from POST: ${JSON.stringify(data)}`;
            manchester.textContent = 'manchester1111';
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

    function fetchCars() {
        getLondon();
        getManchester(); 
    }
    
    function fetchTrucks() {
        getManchester();
    }

    const carSelector = document.getElementById('carSelector');
    const truckSelector = document.getElementById('truckSelector');

    const container = document.getElementById('container');

    const manchester = document.getElementById('manchester');
    const london = document.getElementById('london');
    
    carSelector.addEventListener('click', () => {
        fetchCars();
        container.style.display = 'grid';
        london.style.display = 'block';
        manchester.style.display = 'block';
    });

    truckSelector.addEventListener('click', () => {
        // Specify which element to hide here
        fetchTrucks();
        container.style.display = 'grid';
        london.style.display = 'none';
        manchester.style.display = 'block';

    });     
});
   