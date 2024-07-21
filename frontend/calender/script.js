document.addEventListener('DOMContentLoaded', function() {
    // Initialize Flatpickr for inline display

    const uuidDateMap = new Map([
        ['e2cde5a1-61f4-4b51-bc7c-78f8d6b86ae6', '2024-07-22T08:00:00Z'],
        ['f6e68b47-43c6-4e0b-b622-3d6c747ae3f0', '2024-07-23T08:00:00Z'],
        ['f6e68b47-43c6-4e0b-b622-3d6c747ae3s0', '2024-07-23T09:00:00Z'],
        ['a5c02a7b-3b91-4d87-bcde-18d8a740223e', '2024-07-25T08:00:00Z']
    ]);

    // Convert Map to Flatpickr-compatible format
    const formattedDates = Array.from(uuidDateMap.values()).map(date => date.split('T')[0]);
    const uuidMap = new Map(Array.from(uuidDateMap.entries()).map(([uuid, date]) => [date.split('T')[0], uuid]));
    const dateTimeMap = parseDate();


    flatpickr("#calendar", {
        inline: true,
        enableTime: false,
        enable: formattedDates, 
        dateFormat: "Y-m-d", 
        defaultHour: 12, 
        weekNumbers: true,
        onChange: function(selectedDates, dateStr, instance) {
            const selectedDate = dateStr;
            const uuid = uuidMap.get(selectedDate);
            if (uuid) {
                console.log(uuid +'   '+ selectedDate)
                console.log('Test     '+uuidDateMap.valueOf('2024-07-22T08:00:00Z'));
            } else {
            }
        }
    });

    const availableTimes = [
        '08:00','09:30', '16:30'
    ];

    flatpickr("#time-picker", {
        inline: true,
        enableTime: true,
        noCalendar: true,
        dateFormat: "H:i",
        time_24hr: true,
        enable: availableTimes.map(time => ({ from: time, to: time })),
        minuteIncrement: 30,
        onChange: function(selectedDates, dateStr, instance) {
            console.log('Selected Time:', dateStr);
        }
    });

    function processText() {
        // Get the value from the textarea
        return document.getElementById('textInput').value;
    }


    document.getElementById('book-button').addEventListener('click', function() {
        const checkbox = document.getElementById('confirmCheckbox');
        if(!checkbox.checked) {
            window.alert("You must confirm checkbox");
            window.location.reload();
            return;
        };

        console.log(processText());
        
        window.alert('Thank you for booking ' + processText());
        window.location.reload();
        


        // const bookingTime = document.getElementById('booking-time').value;
        // if (bookingTime) {
        //     console.log('Booking time:', bookingTime);
        //     // You can now send this booking time to your server or handle it as needed
        //     alert('Booking time: ' + bookingTime);
        // } else {
        //     alert('Please select a booking time.');
        // }
    });

    function parseDate() {
        const dateTimeMap = new Map();
        uuidDateMap.forEach((dateString, uuid) => {
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
    const valueToFind = '2024-07-25T08:00:00Z';
    const key = getKeyByValue(uuidDateMap, valueToFind);
    console.log(key);    
});