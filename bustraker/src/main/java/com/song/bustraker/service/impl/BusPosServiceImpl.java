package com.song.bustraker.service.impl;

import com.song.bustraker.dto.BusPosDto;
import com.song.bustraker.service.BusPosService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusPosServiceImpl implements BusPosService {

    @Value("${bus.api.key}")
    private String serviceKey;

    private static final String API_URL =
        "http://openapitraffic.daejeon.go.kr/api/rest/busposinfo/getBusPosByRtid";

    @Override
    public List<BusPosDto> getBusPositions(String busRouteId) {
        List<BusPosDto> busList = new ArrayList<>();
        try {
            // serviceKey는 properties에 저장된 값을 그대로 사용 (이미 인코딩돼 있으면 그대로)
            String urlStr = API_URL + "?busRouteId=" + busRouteId + "&serviceKey=" + serviceKey;
            System.out.println("BusPos 요청 URL: " + urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            InputStream inputStream = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                    ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            conn.disconnect();

            String responseXml = sb.toString();
            System.out.println("BusPos 응답 XML: " + responseXml);

            // XML 파싱
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            // API 응답에서 각 버스 항목은 <itemList>에 들어옴 (환경에 따라 item 또는 itemList인 경우가 있으니 둘 다 체크할 수 있음)
            NodeList nList = doc.getElementsByTagName("itemList");
            if (nList.getLength() == 0) {
                // 혹시 item 태그로 오면 대비
                nList = doc.getElementsByTagName("item");
            }

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element el = (Element) node;
                BusPosDto bus = new BusPosDto();

                bus.setArrTime(getTagValue("ARR_TIME", el));
                bus.setBusNodeId(getTagValue("BUS_NODE_ID", el));
                bus.setBusStopId(getTagValue("BUS_STOP_ID", el));
                bus.setDir(parseIntOrDefault(getTagValue("DIR", el), 0));        // 0=상행, 1=하행
                bus.setEvtCd(getTagValue("EVT_CD", el));
                bus.setGpsLati(getTagValue("GPS_LATI", el));
                bus.setGpsLong(getTagValue("GPS_LONG", el));
                bus.setPlateNo(getTagValue("PLATE_NO", el));
                bus.setRouteCd(getTagValue("ROUTE_CD", el));
                bus.setStreDt(getTagValue("STRE_DT", el));
                bus.setTotalDist(getTagValue("TOTAL_DIST", el));
                bus.setUdType(parseIntOrDefault(getTagValue("ud_type", el), 0));

                busList.add(bus);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return busList;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList == null || nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0);
        if (node == null || node.getFirstChild() == null) return null;
        return node.getFirstChild().getNodeValue().trim();
    }

    private static int parseIntOrDefault(String s, int def) {
        if (s == null) return def;
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ex) {
            return def;
        }
    }
}
