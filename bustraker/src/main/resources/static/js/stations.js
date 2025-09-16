// stations.js
async function fetchStations(routeId) {
    try {
        // 1️⃣ 정류장 목록 가져오기
        const res = await fetch(`/api/stations?busRouteId=${routeId}`);
        const stations = await res.json();

        const stationsList = document.getElementById('stationsList');
        stationsList.innerHTML = ''; // 기존 내용 제거

        for (const station of stations) {
            const li = document.createElement('li');
            li.textContent = station.stopName;

            // 2️⃣ 정류장별 도착 정보 가져오기
            const arrivalRes = await fetch(`/api/arrivalByStop?busStopId=${station.stopId}`);
            const arrivals = await arrivalRes.json();

            // 3️⃣ 특정 노선 필터링 (예: 현재 routeId)
            const routeArrivals = arrivals.filter(a => a.routeNo === routeId);

            if (routeArrivals.length > 0) {
                const span = document.createElement('span');
                span.style.marginLeft = '10px';
                span.textContent = routeArrivals.map(a => `${a.extimeMin}분`).join(', ');
                li.appendChild(span);
            } else {
                const span = document.createElement('span');
                span.style.marginLeft = '10px';
                span.textContent = '-'; // 해당 노선 버스 없음
                li.appendChild(span);
            }

            stationsList.appendChild(li);
        }
    } catch (err) {
        console.error(err);
        document.getElementById('stationsList').innerHTML = '<li>정류장 정보를 불러오지 못했습니다.</li>';
    }
}

// 페이지 로드 시 전체 노선 가져오기
async function fetchRoutes() {
    try {
        const res = await fetch('/api/routes');
        const routes = await res.json();
        const routesList = document.getElementById('routesList');
        routesList.innerHTML = '';

        routes.forEach(route => {
            const li = document.createElement('li');

            // 기존 a태그
            const a = document.createElement('a');
            a.textContent = route.routeName;
            a.onclick = () => fetchStations(route.routeId);
            li.appendChild(a);

            //  ➕ 이동 버튼 생성
            const btn = document.createElement('button');
            btn.textContent = '정류장 보기';
            btn.style.marginLeft = '10px';
            btn.onclick = () => {
                // stations 페이지로 이동하며 routeId 전달
                window.location.href = `/stations?routeId=${route.routeId}`;
            };
            li.appendChild(btn);

            routesList.appendChild(li);
        });
    } catch (err) {
        console.error(err);
        document.getElementById('routesList').innerHTML = '<li>노선 정보를 불러오지 못했습니다.</li>';
    }
}

window.onload = fetchRoutes;
