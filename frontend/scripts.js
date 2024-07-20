document.addEventListener('DOMContentLoaded', (event) => {
    loadInitialData();
});

function loadInitialData() {
    // Make a POST request when the page loads
    fetch('http://localhost:8080/api/v1/load-initial-data', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: 'Page loaded' })
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}

function bookTime() {
    const date = document.getElementById('bookingDate').value;
    const time = document.getElementById('bookingTime').value;

    if (!date || !time) {
        alert('Please select a date and time for booking.');
        return;
    }

    const bookingDetails = {
        date: date,
        time: time
    };

    fetch('http://localhost:8080/api/v1/book-time', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bookingDetails)
    })
    .then(response => response.json())
    .then(data => {
        alert('Booking successful!');
        console.log('Success:', data);
    })
    .catch((error) => {
        alert('Booking failed. Please try again.');
        console.error('Error:', error);
    });
}