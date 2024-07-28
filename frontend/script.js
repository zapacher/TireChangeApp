document.addEventListener('DOMContentLoaded', () => {
    let locationVehicleMap = new Map();
    const allAvailableByLocation = new Map();
    let timeSelected = null;

    function getAvailableLocations() {
        fetch('http://172.33.0.1:9006/tire_change/availableLocations', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    window.alert(errorData.message)
                    throw new Error(errorData);
                });
            }
            return response.json();
        })
        .then(data => {
            locationVehicleMap = new Map(Object.entries(data));
        })
        .catch((error) => {
            console.error('Error with GET request:', error);
            window.location.reload();
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
            locationDiv.appendChild(calendarDiv);

            const timeSelectorContainerDiv = document.createElement('div');
            timeSelectorContainerDiv.id = 'time-selector-container-'+locationLow;
            locationDiv.appendChild(timeSelectorContainerDiv);

            const bookingDetailsDiv = document.createElement('div');
            bookingDetailsDiv.className = 'booking-details-'+locationLow;
            locationDiv.appendChild(bookingDetailsDiv);

            const timeSelectorDiv = document.createElement('div');
            timeSelectorDiv.id = 'time-selector-'+locationLow;
            timeSelectorContainerDiv.appendChild(timeSelectorDiv);

            const textAreaInfo = document.createElement('textarea');
            textAreaInfo.id = 'info-input-'+locationLow;
            textAreaInfo.placeholder = "Please provide contact information...";
            bookingDetailsDiv.appendChild(textAreaInfo);

            const checkboxContainerDiv = document.createElement('div');
            checkboxContainerDiv.className = 'checkbox-container-'+locationLow;
            bookingDetailsDiv.appendChild(checkboxContainerDiv);

            const checboxConfirm = document.createElement('input');
            checboxConfirm.type = 'checkbox';
            checboxConfirm.id = 'confirmCheckbox-'+locationLow;
            checkboxContainerDiv.appendChild(checboxConfirm);

            checboxConfirm.addEventListener('change', function() {
                if (this.checked) {
                    reserveButton.style.display = 'block';
                } else {
                    reserveButton.style.display = 'none';
                }
            });

            const checkboxLabel = document.createElement('label');
            checkboxLabel.htmlFor = 'confirmCheckbox-'+locationLow;
            checkboxLabel.textContent = 'Accept policy';
            checkboxContainerDiv.appendChild(checkboxLabel);
            
            const reserveButton = document.createElement('button');
            reserveButton.id = 'button-' + locationLow;
            reserveButton.textContent = 'Reserve';
            reserveButton.style.display = 'none';
            locationDiv.appendChild(reserveButton);


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

                const time = timeSelected;
                const date = dateSelected;
                
                if(time=="" || date==null) {
                    window.alert("Please select correct time before reserving");
                }
                
                const fullDate = date+'T'+time+':00Z';
        
                const id = getKeyByValue(allAvailableByLocation.get(locationLow), fullDate);
        
                if(id==null) {
                    window.alert("Please contact support");
                }
                
                booking(id, location, info);

                window.alert('Reserved on ' + date + ' at ' + time + ' Have a Nice day! :)');
                window.location.reload();
            });
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

        let jsonRequest = JSON.stringify({
            location: location,
            userTime: getCurrentLocalTimeISO()
        });

        let response;
        fetch('http://172.33.0.1:9006/tire_change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: jsonRequest 
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    document.getElementById(location.toLowerCase()).style.display = 'none';
                    throw new Error(errorData.message);
                });
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
        let jsonRequest = JSON.stringify({
            id: id,
            location: location.toUpperCase(),
            info: info
        });

        fetch('http://172.33.0.1:9006/tire_change/booking', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: jsonRequest
        })
        .then(response => {
            if (!response.ok) {
                response.json().then(errorData => {
                    window.alert(errorData.message)
                    throw new Error(errorData.message);
                });
            }
            return response.json();
        })
        .then(data => {
            if(!data.booked) {
                window.alert("Something went wrong, pleas try again or contact support")

            }
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
            window.location.reload();
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
                    option.style.backgroundColor = 'white'
                    timeSelected = div.dataset.time;
                });
                div.style.backgroundColor = '#d0d0d0'
                document.getElementById('info-input-'+location).style.display = 'block';
                document.getElementsByClassName('checkbox-container-'+location)[0].style.display = 'block';
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
            const dateKey = date.toISOString().split('T')[0];
            const time = dateString.split('T')[1].split('Z')[0]; 

            const formattedTime = time.substring(0, 5);
            if (!dateTimeMap.has(dateKey)) {
                dateTimeMap.set(dateKey, []);
            }

            const times = dateTimeMap.get(dateKey);
            if (!times.includes(formattedTime)) {
                times.push(formattedTime);
            }
        });

        dateTimeMap.forEach((times, date) => {
            times.sort();
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
        return null;
    }

    function getCurrentLocalTimeISO() {
        let date = new Date();

        // Extract the date and time components
        let year = date.getFullYear();
        let month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-based
        let day = String(date.getDate()).padStart(2, '0');
        let hours = String(date.getHours()).padStart(2, '0');
        let minutes = String(date.getMinutes()).padStart(2, '0');
        let seconds = String(date.getSeconds()).padStart(2, '0');
        let milliseconds = String(date.getMilliseconds()).padStart(3, '0');
    
        // Construct ISO 8601 string, maintaining the original local time
        let localISOTime = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.${milliseconds}Z`;
        return localISOTime;
    }

});
   