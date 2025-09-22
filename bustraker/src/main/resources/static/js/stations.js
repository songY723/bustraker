document.addEventListener("DOMContentLoaded", function () {
  const params = new URLSearchParams(window.location.search);
  const routeId = params.get("busRouteId");

  if (routeId) {
    loadData(routeId);
  } else {
    document.getElementById("stationTable").innerHTML =
      "<tr><td colspan='6'>노선이 선택되지 않았습니다.</td></tr>";
  }
});

let busPositions = [];

// 🚍 데이터 로딩
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
      "<tr><td colspan='6'>데이터를 불러오지 못했습니다.</td></tr>";
  }
}

// 🕒 도착 시간 포맷 (yyyyMMddHHmmss → 남은시간)
function formatArrTime(arrTime) {
  if (!arrTime) return "-";

  try {
    const year = parseInt(arrTime.substring(0, 4));
    const month = parseInt(arrTime.substring(4, 6)) - 1; // JS는 0부터 시작
    const day = parseInt(arrTime.substring(6, 8));
    const hour = parseInt(arrTime.substring(8, 10));
    const min = parseInt(arrTime.substring(10, 12));
    const sec = parseInt(arrTime.substring(12, 14));

    const targetDate = new Date(year, month, day, hour, min, sec);
    const now = new Date();

    const diffSec = Math.floor((targetDate - now) / 1000);

 
    //if (diffSec <= 60) return "곧 도착";

    const m = Math.floor(diffSec / 60);
    const s = diffSec % 60;
    return `${m}분 ${s}초`;
  } catch (e) {
    console.error("formatArrTime error:", e, arrTime);
    return "-";
  }
}

// 📋 테이블 렌더링
function renderTable(stations) {
  const table = document.getElementById("stationTable");
  table.innerHTML = "";

  if (stations.length === 0) {
    table.innerHTML = "<tr><td colspan='6'>정류장이 없습니다.</td></tr>";
    return;
  }

  // 정류장 반 나누기 (상행/하행)
  const half = Math.ceil(stations.length / 2);
  const upStations = stations.slice(0, half);
  const downStations = stations.slice(half);

  for (let i = 0; i < half; i++) {
    const up = upStations[i];
    const down = downStations[i] || {}; // 하행이 부족하면 빈 값 처리

    const tr = document.createElement("tr");

    // ⏱ 상행 도착시간
    const tdUpTime = document.createElement("td");
    const busesUp = busPositions.filter(
      (bus) => bus.busStopId === up?.stopId && Number(bus.dir) === 0
    );
    tdUpTime.textContent = busesUp[0] ? formatArrTime(busesUp[0].arrTime) : "-";
    tr.appendChild(tdUpTime);

    // 🚌 상행 버스
    const tdUpBus = document.createElement("td");
    if (busesUp.length > 0) {
      busesUp.forEach((bus) => {
        const span = document.createElement("span");
        span.className = "bus";
        span.textContent = bus.plateNo;
        tdUpBus.appendChild(span);
      });
    } else {
      tdUpBus.textContent = "-";
    }
    tr.appendChild(tdUpBus);

    // 🚏 상행 정류장
    const tdUpStation = document.createElement("td");
    tdUpStation.textContent = up ? up.stopName : "-";
    tr.appendChild(tdUpStation);

    // 🚏 하행 정류장
    const tdDownStation = document.createElement("td");
    tdDownStation.textContent = down.stopName || "-";
    tr.appendChild(tdDownStation);

    // 🚌 하행 버스
    const tdDownBus = document.createElement("td");
    const busesDown = busPositions.filter(
      (bus) => bus.busStopId === down?.stopId && Number(bus.dir) === 1
    );
    if (busesDown.length > 0) {
      busesDown.forEach((bus) => {
        const span = document.createElement("span");
        span.className = "bus";
        span.textContent = bus.plateNo;
        tdDownBus.appendChild(span);
      });
    } else {
      tdDownBus.textContent = "-";
    }
    tr.appendChild(tdDownBus);

    // ⏱ 하행 도착시간
    const tdDownTime = document.createElement("td");
    tdDownTime.textContent = busesDown[0]
      ? formatArrTime(busesDown[0].arrTime)
      : "-";
    tr.appendChild(tdDownTime);

    table.appendChild(tr);
  }
}
