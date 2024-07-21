document.addEventListener('DOMContentLoaded', function() {
    let dateSelected;

    // Initialize Flatpickr for inline display

    const uuidDateMap = new Map([
        ['e2cde5a1-61f4-4b51-bc7c-78f8d6b86ae6', '2024-07-22T08:00:00Z'],
        ['f6e68b47-43c6-4e0b-b622-3d6c747ae3f0', '2024-07-23T08:00:00Z'],
        ['f6e68b47-43c6-4e0b-b622-3d6c747ae3s0', '2024-07-23T09:00:00Z'],
        ['a5c02a7b-3b91-4d87-bcde-18d8a740223e', '2024-07-25T08:00:00Z']
    ]);
    console.log(uuidDateMap)

    // Convert Map to Flatpickr-compatible format
    const formattedDates = Array.from(uuidDateMap.values()).map(date => date.split('T')[0]);
    const uuidMap = new Map(Array.from(uuidDateMap.entries()).map(([uuid, date]) => [date.split('T')[0], uuid]));
    // const dateTimeMap = parseDate();


    flatpickr("#calendar", {
        inline: true,
        enableTime: false,
        enable: formattedDates, 
        dateFormat: "Y-m-d", 
        defaultHour: 12, 
        weekNumbers: true,
        onChange: function(selectedDates, dateStr, instance) {
            dateSelected = dateStr;
            timePicker(parseDate(uuidDateMap, dateStr).get(dateStr));
        }
    });

    function timePicker(times) {
        const timeSelector = document.getElementById('time-selector');
        timeSelector.innerHTML = '';
        // const times = parseDate(uuidDateMap.get(dateSelected));
        console.log(times)
        

        times.forEach(time => {
            const div = document.createElement('div');
            div.textContent = time;
            div.className = 'time-option';
            div.dataset.time = time;
            div.addEventListener('click', () => {
                document.querySelectorAll('.time-option').forEach(option => {
                    option.classList.remove('selected');
                });
                div.classList.add('selected');
            });
            timeSelector.appendChild(div);
        });
    }



    document.getElementById('book-button').addEventListener('click', function() {
        const checkbox = document.getElementById('confirmCheckbox');
        if(!checkbox.checked) {
            window.alert("You must confirm checkbox");
            return;
        };
        const info = document.getElementById('textInput').value;
        const time = document.querySelector('.time-option.selected').dataset.time;
        const date = dateSelected;
        const fullDate = date+'T'+time+':00Z';

        console.log(fullDate);


        const id = getKeyByValue(uuidDateMap, fullDate);

        console.log('ID : '  + id);

        window.alert('Thank you for booking ' + info + ' ' + fullDate);
        window.location.reload();
        
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