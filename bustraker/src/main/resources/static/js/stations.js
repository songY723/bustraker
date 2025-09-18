console.log("stations.js loaded");

document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    // busRouteId로 받아오기
    const routeId = params.get('busRouteId');  
    console.log("현재 URL:", window.location.href);
    console.log("busRouteId 값:", routeId);
    if(routeId) {
        fetchStationsInline(routeId);
    } else {
        document.getElementById('stationsList').innerHTML = '<li>노선이 선택되지 않았습니다.</li>';
    }
});

// routes.js에서 쓰던 그대로 복사
async function fetchStationsInline(routeId) {
    try {
        const res = await fetch(`/api/stations?busRouteId=${routeId}`);
        const stations = await res.json();

        const stationsList = document.getElementById('stationsList');
        if (!stationsList) return;
        stationsList.innerHTML = '';

        stations.forEach(station => {
            const li = document.createElement('li');
            li.textContent = station.stopName;
            stationsList.appendChild(li);
        });
    } catch (err) {
        console.error(err);
    }
}