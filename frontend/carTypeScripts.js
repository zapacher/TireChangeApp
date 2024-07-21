document.addEventListener('DOMContentLoaded', () => {

    let locationVehicleMap = new Map();

    function getAvailableLocations() {
        const element = document.getElementById(location);
        fetch('http://localhost:8080/tire_change/availableLocations', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    element.textContent = errorData.message;
                });
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            locationVehicleMap = new Map(Object.entries(data));
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

    getAvailableLocations();

    function booking(id, location, info) {
        const element = document.getElementById(location);
        fetch('http://localhost:8080/tire_change/booking', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: '{"id": "'+id+'","location": "'+location.toUpperCase()+'","info": "'+info+'"}'
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    element.textContent = errorData.message;
                });
                throw new Error('Network response was not ok ' + response.statusText);
            }
        })
        .then(data => {
            console.log('BOOKING -> ' + data)
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

    function createLocationDiv(location) {
        return '<div id="'+location.toLowerCase()+'" class="element" ></div>'
    }

    function processBookintAvailability(locations) {
        document.getElementById('container').innerHTML = '';

        locations.forEach((location) => {
            document.getElementById('container').innerHTML += createLocationDiv(location);
            document.getElementById(location.toLowerCase()).style.display = 'none';
        });
        
        locations.forEach((location) => {
            getAvailableBooking(location);
            document.getElementById(location.toLowerCase()).style.display = 'block';
        });
    }    

    function getLocationsForVehicle(vehicleType) {
        let locations = [];

        locationVehicleMap.forEach((vehicleTypes, location) => {
            if (vehicleTypes.includes(vehicleType)) {
                locations.push(location);
            }
        });

        return locations;
    }

    function getAvailableBooking(location) {
        let element = document.getElementById(location.toLowerCase());

        fetch('http://localhost:8080/tire_change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: '"'+location+'"'
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    element.textContent = errorData.message;
                });
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            element.innerHTML = 
                `<p>Address: ${data.address}</p>
                <p>Location: ${data.location}</p>
                <p>Vehicle Types: ${data.vehicleTypes.join(', ')}</p>`;
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

    function createCalender() {}
    function fillCalender(calenderData) {}

    const bookTime = document.getElementById('bookTime');

    const carSelector = document.getElementById('carSelector');
    const truckSelector = document.getElementById('truckSelector');

    bookTime.addEventListener('click', () => {
        booking(id, location, info);
    })

    carSelector.addEventListener('click', () => {
        const locations = getLocationsForVehicle('CAR');
        processBookintAvailability(locations);
    });

    truckSelector.addEventListener('click', () => {
        const locations = getLocationsForVehicle('TRUCK');
        processBookintAvailability(locations);
    }); 

});
   