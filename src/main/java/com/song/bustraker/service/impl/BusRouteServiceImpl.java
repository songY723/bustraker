package com.song.bustraker.service.impl;

import com.song.bustraker.dto.BusRouteDto;
import com.song.bustraker.service.BusRouteService;
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
public class BusRouteServiceImpl implements BusRouteService {

    @Value("${bus.api.key}")
    private String serviceKey;

    private static final String API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getRouteInfoAll";

    @Override
    public List<BusRouteDto> getAllRoutes() {
        List<BusRouteDto> routes = new ArrayList<>();
        try {
            String urlStr = API_URL + "?serviceKey=" + serviceKey + "&reqPage=1";
           

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/xml");

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
            

            routes = parseRoutes(responseXml);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    private List<BusRouteDto> parseRoutes(String xml) {
        List<BusRouteDto> routes = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("itemList");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    BusRouteDto dto = new BusRouteDto();
                    dto.setRouteId(getTagValue("ROUTE_CD", el));
                    dto.setRouteName(getTagValue("ROUTE_NO", el));
                    dto.setRouteType(getTagValue("ROUTE_TP", el));
                    routes.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0);
        if (node == null || node.getFirstChild() == null) return null;
        return node.getFirstChild().getNodeValue().trim();
    }
}

