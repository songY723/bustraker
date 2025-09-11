<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>
<head>
    <title>노선 정류장 목록</title>
</head>
<body>
<h1>선택한 노선의 정류장 목록</h1>

<ul>
<c:forEach var="station" items="${stations}">
    <li>
        ${station.stopName} (ID: ${station.stopId})
        <form action="arrival" method="get" style="display:inline;">
            <input type="hidden" name="stopId" value="${station.stopId}" />
            <button type="submit">도착 정보 보기</button>
        </form>
    </li>
</c:forEach>
</ul>

</body>
</html>