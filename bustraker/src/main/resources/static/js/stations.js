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
    const diffSec = Math.floor((targetDate.getTime() - now.getTime()) / 1000);

    const m = Math.floor(diffSec / 60);
    const s = diffSec % 60;
    return `${m}분 ${s}초`;
  } catch (e) {
    console.error("formatArrTime error:", e, arrTime);
    return "-";
  }
}

// 🚍 데이터 로딩
async function loadData(routeId) {
  try {
    // 1️⃣ 정류장 목록 불러오기
    const resStations = await fetch(`/api/stations?busRouteId=${routeId}`);
    const stations = await resStations.json();

    // 2️⃣ 버스 위치 데이터 불러오기 (노선 내 현재 버스 위치)
    const resBusPos = await fetch(`/api/busPositions?busRouteId=${routeId}`);
    const busList = await resBusPos.json();

    // 3️⃣ 버스 위치 매칭 (정류장 BUS_NODE_ID 기준)
    stations.forEach(st => {
      st.busPresent = busList.some(bus => bus.busNodeId === st.busNodeId);
    });

    // 4️⃣ 각 정류장 도착정보 불러오기 (BUS_NODE_ID 기준)
	const arrivalPromises = stations.map(station =>
	  fetch(`/api/arrivalByStop?busStopId=${station.stopId}&busRouteId=${routeId}`)
	    .then(res => res.json())
	    .then(arr => {
	      if (arr && arr.length > 0) {
	        const a = arr[0];
	        // 백엔드에서 이미 해당 노선만 필터링된 상태
	        station.arrivalText = `${a.extimeMin}분 ${a.extimeSec}초`;
	      } else {
	        station.arrivalText = "-";
	      }
	      return station;
	    })
	);

    await Promise.all(arrivalPromises);

    // 5️⃣ 테이블 렌더링
    renderTable(stations);
  } catch (err) {
    console.error(err);
    document.getElementById("stationTable").innerHTML =
      "<tr><td colspan='6'>데이터를 불러오지 못했습니다.</td></tr>";
  }
}

// 📋 테이블 렌더링
function renderTable(stations) {
  const table = document.getElementById("stationTable");
  table.innerHTML = "";

  if (!stations.length) {
    table.innerHTML = "<tr><td colspan='6'>정류장이 없습니다.</td></tr>";
    return;
  }

  const half = Math.ceil(stations.length / 2);
  const upStations = stations.slice(0, half);
  const downStations = stations.slice(half);

  for (let i = 0; i < half; i++) {
    const up = upStations[i];
    const down = downStations[i] || {};

    const tr = document.createElement("tr");

    // 1️⃣ 상행 도착시간
    const tdUpTime = document.createElement("td");
    tdUpTime.textContent = up?.arrivalText || "-";
    tr.appendChild(tdUpTime);

    // 2️⃣ 상행 🚌 (버스 위치 강조)
    const tdUpBus = document.createElement("td");
    if (up?.busPresent) {
      tdUpBus.innerHTML = "🚌";
      tdUpBus.style.color = "red";
      tdUpBus.style.fontWeight = "bold";
    } else {
      tdUpBus.innerHTML = "-";
    }
    tdUpBus.classList.add("bus");
    tr.appendChild(tdUpBus);

    // 3️⃣ 상행 정류장
    const tdUpStation = document.createElement("td");
    tdUpStation.textContent = up?.stopName || "-";
    tr.appendChild(tdUpStation);

    // 4️⃣ 하행 정류장
    const tdDownStation = document.createElement("td");
    tdDownStation.textContent = down?.stopName || "-";
    tr.appendChild(tdDownStation);

    // 5️⃣ 하행 🚌 (버스 위치 강조)
    const tdDownBus = document.createElement("td");
    if (down?.busPresent) {
      tdDownBus.innerHTML = "🚌";
      tdDownBus.style.color = "blue";
      tdDownBus.style.fontWeight = "bold";
    } else {
      tdDownBus.innerHTML = "-";
    }
    tdDownBus.classList.add("bus");
    tr.appendChild(tdDownBus);

    // 6️⃣ 하행 도착시간
    const tdDownTime = document.createElement("td");
    tdDownTime.textContent = down?.arrivalText || "-";
    tr.appendChild(tdDownTime);

    table.appendChild(tr);
  }
}
