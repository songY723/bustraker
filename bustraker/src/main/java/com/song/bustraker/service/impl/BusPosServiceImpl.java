package com.song.bustraker.service.impl;

import com.song.bustraker.dto.BusPosDto;
import com.song.bustraker.service.BusPosService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusPosServiceImpl implements BusPosService {

    private static final String API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/busposinfo/getBusPosByRtid";

    @Value("${bus.api.key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<BusPosDto> getBusPositions(String busRouteId) throws Exception {
        String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
        String encodedRouteId = URLEncoder.encode(busRouteId, StandardCharsets.UTF_8);

        String url = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("serviceKey", encodedKey)
                .queryParam("busRouteId", encodedRouteId)
                .toUriString();

        System.out.println("요청 URL: " + url);

        String responseXml = restTemplate.getForObject(url, String.class);
        System.out.println("응답 XML: " + responseXml);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("item");
        List<BusPosDto> busList = new ArrayList<>();

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                BusPosDto bus = new BusPosDto();
                bus.setArrTime(getTagValue("ARR_TIME", element));
                bus.setBusNodeId(getTagValue("BUS_NODE_ID", element));
                bus.setBusStopId(getTagValue("BUS_STOP_ID", element));
                bus.setDir(getTagValue("DIR", element));
                bus.setEvtCd(getTagValue("EVT_CD", element));
                bus.setGpsLati(getTagValue("GPS_LATI", element));
                bus.setGpsLong(getTagValue("GPS_LONG", element));
                bus.setPlateNo(getTagValue("PLATE_NO", element));
                bus.setRouteCd(getTagValue("ROUTE_CD", element));
                bus.setStreDt(getTagValue("STRE_DT", element));
                bus.setTotalDist(getTagValue("TOTAL_DIST", element));
                bus.setUdType(getTagValue("ud_type", element));
                busList.add(bus);
            }
        }
        return busList;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0);
        if (node == null || node.getFirstChild() == null) return null;
        return node.getFirstChild().getNodeValue();
    }
}
