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
            console.log(data)
            // displayElement.textContent = `Response from POST: ${JSON.stringify(data)}`;
            // generateCalendar(location);
            // element.textContent = location + locationsInfo.get(location);
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
        container.innerHTML = '';
        for(const location of locations) {
            document.getElementById('container').innerHTML += createLocationDiv(location);
            getAvailableBooking(location);
        }

        // locations.forEach((location) => {
        //     console.log(location)
        //     document.getElementById('container').innerHTML += createLocationDiv(location);
        //     console.log(document.getElementById('container').innerHTML)
        //     getAvailableBooking(location);
        //     // document.getElementById(location.toLowerCase()).style.display = 'block';
        // });
    }    

    function getLocationsForVehicle(vehicleType) {
        let locations = [];
        locationVehicleMap.forEach((vehicleTypes, location) => {
            if (vehicleTypes.includes(vehicleType)) {
                locations.push(location);
            }
        });
        console.log(locations)
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
            // displayElement.textContent = `Response from POST: ${JSON.stringify(data)}`;
            // // generateCalendar(location);
            element.textContent = data.address +'\n'+ data.location +'\n'+ data.vehicleTypes;
            // element.innerText = data.address;
            // element.innerHTML = 
            //     `<p>Address: ${data.address}</p>
            //      <p>Location: ${data.location}</p>
            //      <p>Vehicle Types: ${data.vehicleTypes.join(', ')}</p>`;
            console.log(data.location.toLowerCase())
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

    const carSelector = document.getElementById('carSelector');
    const truckSelector = document.getElementById('truckSelector');

    
    carSelector.addEventListener('click', () => {
        const locations = getLocationsForVehicle('CAR');
        processBookintAvailability(locations);
    });

    truckSelector.addEventListener('click', () => {
        const locations = getLocationsForVehicle('TRUCK');
        processBookintAvailability(locations);
    }); 

});
   