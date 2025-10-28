async function fetchRoutes() {
    try {
        const res = await fetch('/api/routes');
        const routes = await res.json();

        const routesList = document.getElementById('routesList');
        routesList.innerHTML = ''; // 기존 로딩 메시지 제거
        console.log("✅ routes.js loaded");
        console.log(routes[0]);
        // 숫자 정렬
        routes.sort((a, b) => parseInt(a.routeName) - parseInt(b.routeName));
        console.log(routes.map(r => r.routeName));
        // 🔸 그룹 객체
        const grouped = {};
       
        routes.forEach(route => {
            const num = parseInt(route.routeName);

            let groupKey;
            if (num < 100) groupKey = '0~99번대';
            else {
                const hundred = Math.floor(num / 100) * 100;
                groupKey = `${hundred}번대`;
            }

            if (!grouped[groupKey]) grouped[groupKey] = [];
            grouped[groupKey].push(route);
        });
         
        // 🔸 그룹별 박스 렌더링
        for (const [groupName, routesInGroup] of Object.entries(grouped)) {
            const groupDiv = document.createElement('div');
            groupDiv.style.border = '1px solid #ccc';
            groupDiv.style.borderRadius = '10px';
            groupDiv.style.margin = '10px 0';
            groupDiv.style.padding = '10px';
            groupDiv.style.backgroundColor = '#f9f9f9';

            const groupTitle = document.createElement('h3');
            groupTitle.textContent = groupName;
            groupTitle.style.marginBottom = '10px';
            groupDiv.appendChild(groupTitle);

            const ul = document.createElement('ul');
            ul.style.listStyle = 'none';
            ul.style.paddingLeft = '10px';

            routesInGroup.forEach(route => {
                const li = document.createElement('li');
                li.style.marginBottom = '4px';

                const a = document.createElement('a');
                a.textContent = route.routeName;
                a.href = '#';
                a.onclick = () => fetchStationsInline(route.routeId);

                const btn = document.createElement('button');
                btn.textContent = '정류장 보기';
                btn.style.marginLeft = '10px';
                btn.onclick = () => {
                    window.location.href = `/stations?busRouteId=${route.routeId}`;
                };

                li.appendChild(a);
                li.appendChild(btn);
                ul.appendChild(li);
            });

            groupDiv.appendChild(ul);
            routesList.appendChild(groupDiv);
        }
    } catch (err) {
        console.error(err);
        document.getElementById('routesList').innerHTML =
            '<li>노선 정보를 불러오지 못했습니다.</li>';
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
