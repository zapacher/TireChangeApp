document.addEventListener('DOMContentLoaded', () => {
    let locationsInfo = new Map();
    onLoad();

    function getAvailable(location) {
        const element = document.getElementById(location);
        fetch('http://localhost:8080/tire_change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: '"'+location.toUpperCase()+'"'
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
            element.textContent = locationsInfo.get(location);
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
            console.log(data)
            // displayElement.textContent = `Response from POST: ${JSON.stringify(data)}`;
            // generateCalendar(location);
            // element.textContent = location + locationsInfo.get(location);
        })
        .catch((error) => {
            console.error('Error with POST request:', error);
        });
    }


    function fetchCars() {
        getAvailable('london');
        getAvailable('manchester');
    }
    
    function fetchTrucks() {
        getAvailable('manchester');
    }

    const carSelector = document.getElementById('carSelector');
    const truckSelector = document.getElementById('truckSelector');

    const container = document.getElementById('container');

    const london = document.getElementById('london');
    const manchester = document.getElementById('manchester');
    
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

    function generateCalendar(location) {
        const now = new Date();
        const month = now.getMonth(); // 0-11
        const year = now.getFullYear();
        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        
        let calendarHtml = '<div class="calendar-header">' + 
                           now.toLocaleString('default', { month: 'long' }) + ' ' + year + 
                           '</div>';
        calendarHtml += '<div class="calendar-days">' +
                        '<div class="day">Su</div>' +
                        '<div class="day">Mo</div>' +
                        '<div class="day">Tu</div>' +
                        '<div class="day">We</div>' +
                        '<div class="day">Th</div>' +
                        '<div class="day">Fr</div>' +
                        '<div class="day">Sa</div>' +
                        '</div>';
        
        calendarHtml += '<div class="calendar-grid">';
        // Add empty cells for the days before the first day of the month
        for (let i = 0; i < firstDay; i++) {
            calendarHtml += '<div class="date"></div>';
        }
        
        // Add the actual dates
        for (let day = 1; day <= daysInMonth; day++) {
            calendarHtml += '<div class="date">' + day + '</div>';
        }
        
        // Add empty cells for the days after the last day of the month
        const totalCells = firstDay + daysInMonth;
        const remainingCells = 42 - totalCells;
        for (let i = 0; i < remainingCells; i++) {
            calendarHtml += '<div class="date"></div>';
        }
        
        calendarHtml += '</div>';
        document.getElementById(location).innerHTML = calendarHtml;
    }

    function onLoad() {
        locationsInfo.set('london', 'Adress: 1A Gunton Rd, London');
        locationsInfo.set('manchester', 'Adress: 14 Bury New Rd, Manchester');
    }
    
});
   