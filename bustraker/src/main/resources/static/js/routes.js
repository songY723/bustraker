async function fetchRoutes() {
    try {
        const res = await fetch('/api/routes');
        const routes = await res.json();
        const routesList = document.getElementById('routesList');
        routesList.innerHTML = ''; // 기존 로딩 메시지 제거

        routes.forEach(route => {
            const li = document.createElement('li');

            // 기존 a 태그 (노선 클릭 시 stations 정보 로드)
            const a = document.createElement('a');
            a.textContent = route.routeName;
            a.href = "#"; // 필요 시 href 설정
            a.onclick = () => fetchStationsInline(route.routeId); // 기존 기능 그대로

            // 새 버튼 (노선 클릭 후 stations 페이지로 이동)
            const btn = document.createElement('button');
            btn.textContent = '정류장 보기';
            btn.onclick = () => {
                window.location.href = `/stations?busRouteId=${route.routeId}`;
            };

            li.appendChild(a);
            li.appendChild(btn); // a 옆에 버튼 추가
            routesList.appendChild(li);
        });
    } catch (err) {
        console.error(err);
        document.getElementById('routesList').innerHTML = '<li>노선 정보를 불러오지 못했습니다.</li>';
    }
}

// 기존 a 클릭 시 리스트 아래에 stations 보여주기
async function fetchStationsInline(routeId) {
    try {
        const res = await fetch(`/api/stations?busRouteId=${routeId}`);
        const stations = await res.json();
        const stationsList = document.getElementById('stationsList');
        if (!stationsList) return; // stationsList가 없으면 무시
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

// 페이지 로드 시 전체 노선 가져오기
window.onload = fetchRoutes;
