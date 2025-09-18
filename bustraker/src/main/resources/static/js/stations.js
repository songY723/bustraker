document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    const routeId = params.get("busRouteId");

    if (routeId) {
        fetchStations(routeId);
    } else {
        document.getElementById("stationsUp").innerHTML = '<li>노선이 선택되지 않았습니다.</li>';
        document.getElementById("stationsDown").innerHTML = '<li>노선이 선택되지 않았습니다.</li>';
    }
});

async function fetchStations(routeId) {
    try {
        const res = await fetch(`/api/stations?busRouteId=${routeId}`);
        const stations = await res.json();

        // udType 대신 dir 사용: 0 = 상행, 1 = 하행 // 안댐 이거
        const up = stations.filter(s => Number(s.udType) === 0);
        const down = stations.filter(s => Number(s.udType) === 1);
        renderStations("stationsUp", up);
        renderStations("stationsDown", down);

    } catch (err) {
        console.error(err);
        document.getElementById("stationsUp").innerHTML = '<li>정류장 정보를 불러오지 못했습니다.</li>';
        document.getElementById("stationsDown").innerHTML = '<li>정류장 정보를 불러오지 못했습니다.</li>';
    }
}

function renderStations(containerId, stations) {
    const list = document.getElementById(containerId);
    list.innerHTML = "";

    if (stations.length === 0) {
        list.innerHTML = "<li>정류장이 없습니다.</li>";
        return;
    }

    stations.forEach((station, idx) => {
        const li = document.createElement("li");
        li.textContent = `${idx + 1}. ${station.stopName} (${station.gpsLati}, ${station.gpsLong})`;
        list.appendChild(li);
    });
}
