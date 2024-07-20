document.addEventListener('DOMContentLoaded', () => {
    // Function to make the initial POST request
    function getLondon() {
        fetch('http://localhost:8080/tire-change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: '{"LONDON"}' })
        })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    }

    function getManchester() {
        fetch('http://localhost:8080/tire-change/getAvailableTime', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: '{"LONDON"}' })
        })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    }

    // Call the function to make the initial POST request
    getLondon();
    getManchester();

    const showButton = document.getElementById('showButton');
    const hideButton = document.getElementById('hideButton');
    const container = document.getElementById('container');
    const element1 = document.getElementById('element1');
    const element2 = document.getElementById('element2');

    showButton.addEventListener('click', () => {
        container.style.display = 'grid';
        element1.style.display = 'block';
        element2.style.display = 'block';
    });

    hideButton.addEventListener('click', () => {
        // Specify which element to hide here
        element1.style.display = 'none';
    });
});