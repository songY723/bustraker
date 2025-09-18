document.addEventListener("DOMContentLoaded", function () {
  const params = new URLSearchParams(window.location.search);
  const routeId = params.get("busRouteId");

  if (routeId) {
    loadData(routeId);
  } else {
    document.getElementById("stationTable").innerHTML =
      "<tr><td colspan='3'>노선이 선택되지 않았습니다.</td></tr>";
  }
});

let busPositions = [];

async function loadData(routeId) {
  try {
    // 1. 정류장 불러오기
    const resStations = await fetch(`/api/stations?busRouteId=${routeId}`);
    const stations = await resStations.json();

    // 2. 버스 위치 불러오기
    const resBus = await fetch(`/api/bus?busRouteId=${routeId}`);
    busPositions = await resBus.json();

    renderTable(stations);
  } catch (err) {
    console.error(err);
    document.getElementById("stationTable").innerHTML =
      "<tr><td colspan='3'>데이터를 불러오지 못했습니다.</td></tr>";
  }
}

function renderTable(stations) {
  const table = document.getElementById("stationTable");
  table.innerHTML = "";

  if (stations.length === 0) {
    table.innerHTML = "<tr><td colspan='3'>정류장이 없습니다.</td></tr>";
    return;
  }

  stations.forEach((station, idx) => {
    const tr = document.createElement("tr");

    // 정류장명
    const tdName = document.createElement("td");
    tdName.textContent = `${idx + 1}. ${station.stopName}`;
    tr.appendChild(tdName);

    // 상행 버스
    const tdUp = document.createElement("td");
    const busesUp = busPositions.filter(
      (bus) => bus.busStopId === station.stopId && Number(bus.dir) === 0
    );
    if (busesUp.length > 0) {
      busesUp.forEach((bus) => {
        const span = document.createElement("span");
        span.className = "bus";
        span.textContent = bus.plateNo;
        tdUp.appendChild(span);
      });
    } else {
      tdUp.textContent = "-";
    }
    tr.appendChild(tdUp);

    // 하행 버스
    const tdDown = document.createElement("td");
    const busesDown = busPositions.filter(
      (bus) => bus.busStopId === station.stopId && Number(bus.dir) === 1
    );
    if (busesDown.length > 0) {
      busesDown.forEach((bus) => {
        const span = document.createElement("span");
        span.className = "bus";
        span.textContent = bus.plateNo;
        tdDown.appendChild(span);
      });
    } else {
      tdDown.textContent = "-";
    }
    tr.appendChild(tdDown);

    table.appendChild(tr);
  });
}
