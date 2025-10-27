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

// 🚍 버스 데이터 및 도착정보 로드
async function loadData(routeId) {
  try {
    // 1️⃣ 정류장 목록
    const resStations = await fetch(`/api/stations?busRouteId=${routeId}`);
    const stations = await resStations.json();

    // 2️⃣ 현재 버스 위치
    const resBusPos = await fetch(`/api/busPositions?busRouteId=${routeId}`);
    const busList = await resBusPos.json();

    // 버스 위치 표시
    stations.forEach(st => {
      st.busPresent = busList.some(bus => bus.busNodeId === st.busNodeId);
    });

    // 3️⃣ 각 정류장 도착 정보 불러오기
    const arrivalPromises = stations.map(station =>
  fetch(`/api/arrivalByStop?busStopId=${station.stopId}&busRouteId=${routeId}`)
    .then(res => res.json())
    .then(arr => {
      if (arr && arr.length > 0) {
        const a = arr[0];
        let extimeMin = 0;

        // extimeMin > 0이면 그대로 사용
        if (a.extimeMin > 0) {
          extimeMin = a.extimeMin;
        } 
        // extimeMin 0이면 extimeSec를 분으로 변환
        else if (a.extimeSec > 0) {
          extimeMin = Math.ceil(a.extimeSec / 60);
        }

        // 도착 텍스트 설정
        if (extimeMin > 0) {
          station.arrivalText = `${extimeMin}분`;
        } else {
          station.arrivalText = "곧 도착";
        }
      } else {
        station.arrivalText = "-";
      }
      return station;
    })
    .catch(() => {
      station.arrivalText = "-";
      return station;
    })
);

    await Promise.all(arrivalPromises);

    // 4️⃣ 테이블 렌더링
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

    // 2️⃣ 상행 🚌
    const tdUpBus = document.createElement("td");
    tdUpBus.innerHTML = up?.busPresent ? "🚌" : "-";
    tdUpBus.style.color = up?.busPresent ? "red" : "";
    tdUpBus.style.fontWeight = up?.busPresent ? "bold" : "";
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

    // 5️⃣ 하행 🚌
    const tdDownBus = document.createElement("td");
    tdDownBus.innerHTML = down?.busPresent ? "🚌" : "-";
    tdDownBus.style.color = down?.busPresent ? "blue" : "";
    tdDownBus.style.fontWeight = down?.busPresent ? "bold" : "";
    tdDownBus.classList.add("bus");
    tr.appendChild(tdDownBus);

    // 6️⃣ 하행 도착시간
    const tdDownTime = document.createElement("td");
    tdDownTime.textContent = down?.arrivalText || "-";
    tr.appendChild(tdDownTime);

    table.appendChild(tr);
  }
}
