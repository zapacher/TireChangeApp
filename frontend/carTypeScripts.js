document.addEventListener('DOMContentLoaded', () => {

    let locationVehicleMap = new Map();
    let allAvailableByLocation = new Map();

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


    function createLocationDiv(location) {
        const div = document.createElement('div');
        div.className = 'element';
        div.id = location;
        return div;
    }

    const availableBooking = new Map();

    function processBookintAvailability(locations) {
        availableBooking.clear();
        document.getElementById('container').innerHTML = '';

        locations.forEach(location => {
            const locationLow = location.toLowerCase();
            const locationDiv = createLocationDiv(locationLow);
            
            const infoDiv = document.createElement('div');
            infoDiv.id = 'info-'+locationLow;
            locationDiv.appendChild(infoDiv);
            
            document.getElementById('container').appendChild(locationDiv);
            document.getElementById(locationLow).style.display = 'none';
            const calendarDiv = document.createElement('div');
            calendarDiv.id = 'calendar-'+location;
            locationDiv.appendChild(calendarDiv)

            const bookingDetailsDiv = document.createElement('div');
            bookingDetailsDiv.className = 'booking-details-'+locationLow;
            locationDiv.appendChild(bookingDetailsDiv);

            const timeSelectorContainerDiv = document.createElement('div');
            timeSelectorContainerDiv.id = 'time-selector-container-'+locationLow;
            locationDiv.appendChild(timeSelectorContainerDiv);

            const timeSelectorDiv = document.createElement('div');
            timeSelectorDiv.id = 'time-selector-'+locationLow;
            timeSelectorContainerDiv.appendChild(timeSelectorDiv);

            const textAreaInfo = document.createElement('textarea');
            textAreaInfo.id = 'info-input-'+locationLow;
            textAreaInfo.placeholder = "Please enter contact information...";
            bookingDetailsDiv.appendChild(textAreaInfo);

            const checkboxContainerDiv = document.createElement('div');
            checkboxContainerDiv.className = 'checkbox-container-'+locationLow;
            bookingDetailsDiv.appendChild(checkboxContainerDiv);

            const checboxConfirm = document.createElement('input');
            checboxConfirm.type = 'checkbox';
            checboxConfirm.id = 'confirmCheckbox-'+locationLow;
            checkboxContainerDiv.appendChild(checboxConfirm);

            const checkboxLabel = document.createElement('label');
            checkboxLabel.htmlFor = 'confirmCheckbox-'+locationLow;
            checkboxLabel.textContent = 'Accept';
            checkboxContainerDiv.appendChild(checkboxLabel);
            
            const reserveButton = document.createElement('button');
            reserveButton.id = 'button' + locationLow;
            reserveButton.textContent = 'Reserve';

            reserveButton.addEventListener('click', function() {
                const checkbox = document.getElementById('confirmCheckbox-'+locationLow);
                if(!checkbox.checked) {
                    window.alert("You must accept our policy before resorveing");
                    return;
                };
                
                const info = document.getElementById('info-input-'+locationLow).value;
                if(info==="") {
                    window.alert("Please provide contact information");
                    return;
                }

                const time = document.querySelector('.time-option.selected-'+locationLow).dataset.time;
                const date = dateSelected;
                
                if(time=="" || date==null) {
                    window.alert("Please select correct time before reserving");
                }
                
                const fullDate = date+'T'+time+':00Z';
        
                console.log(fullDate);
        
                console.log(allAvailableByLocation.get(locationLow));
        
                const id = getKeyByValue(allAvailableByLocation.get(locationLow), fullDate);
        
                console.log('ID : '  + id);

                if(id==null) {
                    window.alert("Please contact support");
                }
                
                booking(id, location, info);

                // window.alert('Reserved at ' + date + ' at ' + fullDate);
                // window.location.reload();
            });

            locationDiv.appendChild(reserveButton);



        });
        
        locations.forEach(location => {
            getAvailableBooking(location);
        });

        locations.forEach(async location => {
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

    let dateSelected;

    function getAvailableBooking(location) {
        allAvailableByLocation.clear();
        let element = document.getElementById(location.toLowerCase());

        let response;
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
            response = data;
              document.getElementById('info-'+location.toLowerCase()).innerHTML = `
                <p>Address: ${response.address}</p>
                <p>Location: ${response.location}</p>
                <p>Vehicle Types: ${response.vehicleTypes.join(', ')}</p>
            `;

            const availableTimesMap = new Map();
            response.availableTimeList.forEach(item => {
                availableTimesMap.set(item.id, item.availableTime)});

            allAvailableByLocation.set(location.toLowerCase(), availableTimesMap);

            const formattedDates = Array.from(availableTimesMap.values()).map(date => date.split('T')[0]);

            flatpickr("#calendar-"+location, {
                inline: true,
                enableTime: false,
                enable: formattedDates, 
                dateFormat: "Y-m-d", 
                defaultHour: 12, 
                weekNumbers: true,
                onChange: function(selectedDates, dateStr, instance) {
                    dateSelected = dateStr;
                    timePicker(parseDate(availableTimesMap, dateStr).get(dateStr), location.toLowerCase());
                }
            });
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }

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

    function timePicker(times, location) {
        const timeSelector = document.getElementById('time-selector-'+location);
        timeSelector.innerHTML = '';

        times.forEach(time => {
            const div = document.createElement('div');
            div.textContent = time;
            div.className = 'time-option';
            div.dataset.time = time;
            div.addEventListener('click', () => {
                document.querySelectorAll('.time-option').forEach(option => {
                    option.classList.remove('selected-'+location);
                });
                div.classList.add('selected-'+location);
            });
            timeSelector.appendChild(div);
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

    function parseDate(dateList) {
        const dateTimeMap = new Map();
        dateList.forEach((dateString, uuid) => {
            const date = new Date(dateString);
            const dateKey = date.toISOString().split('T')[0]; // Get date part (YYYY-MM-DD)
            const time = dateString.split('T')[1].split('Z')[0]; // Get time part (HH:mm:ss)

            const formattedTime = time.substring(0, 5); // Extract HH:mm
            if (!dateTimeMap.has(dateKey)) {
                dateTimeMap.set(dateKey, []);
            }

            const times = dateTimeMap.get(dateKey);
            if (!times.includes(formattedTime)) {
                times.push(formattedTime);
            }
        });

        dateTimeMap.forEach((times, date) => {
            times.sort(); // Optional: sort times
            dateTimeMap.set(date, times);
        });
        return dateTimeMap;
    }

    function getKeyByValue(map, value) {
        for (let [key, val] of map) {
            if (val === value) {
                return key;
            }
        }
        return null; // or undefined, or any other value to indicate not found
    }

});
   