package com.song.bustraker.service.impl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.song.bustraker.dto.ArrivalInfoDto;
import com.song.bustraker.service.BusArrivalService;

public class BusArrivalServiceImpl implements BusArrivalService {

	 @Value("${bus.api.key}")
	    private String serviceKey;

	    private final RestTemplate restTemplate = new RestTemplate();

	    private static final String API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/arrive/getArrInfoByStopID";

	    @Override
	    public List<ArrivalInfoDto> getArrivalInfoByStop(String busStopId) {
	        String url = API_URL + "?serviceKey=" + serviceKey + "&BusStopID=" + busStopId;
	        String responseXml = restTemplate.getForObject(url, String.class);

	        List<ArrivalInfoDto> list = new ArrayList<>();
	        try {
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document doc = dBuilder.parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));
	            doc.getDocumentElement().normalize();

	            NodeList nList = doc.getElementsByTagName("itemList");
	            for (int i = 0; i < nList.getLength(); i++) {
	                Node node = nList.item(i);
	                if (node.getNodeType() == Node.ELEMENT_NODE) {
	                    Element el = (Element) node;
	                    ArrivalInfoDto dto = new ArrivalInfoDto();
	                    dto.setBusNodeId(getTagValue("BUS_NODE_ID", el));
	                    dto.setBusStopId(getTagValue("BUS_STOP_ID", el));
	                    dto.setCarRegNo(getTagValue("CAR_REG_NO", el));
	                    dto.setDestination(getTagValue("DESTINATION", el));
	                    dto.setExtimeMin(parseInt(getTagValue("EXTIME_MIN", el)));
	                    dto.setExtimeSec(parseInt(getTagValue("EXTIME_SEC", el)));
	                    dto.setInfoOfferTm(getTagValue("INFO_OFFER_TM", el));
	                    dto.setLastCat(getTagValue("LAST_CAT", el));
	                    dto.setLastStopId(getTagValue("LAST_STOP_ID", el));
	                    dto.setMsgTp(getTagValue("MSG_TP", el));
	                    dto.setRouteCd(getTagValue("ROUTE_CD", el));
	                    dto.setRouteNo(getTagValue("ROUTE_NO", el));
	                    dto.setRouteTp(getTagValue("ROUTE_TP", el));
	                    dto.setStatusPos(getTagValue("STATUS_POS", el));
	                    dto.setStopName(getTagValue("STOP_NAME", el));
	                    list.add(dto);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return list;
	    }

	    private static String getTagValue(String tag, Element element) {
	        NodeList nodeList = element.getElementsByTagName(tag);
	        if (nodeList.getLength() == 0) return null;
	        Node node = nodeList.item(0);
	        if (node == null || node.getFirstChild() == null) return null;
	        return node.getFirstChild().getNodeValue().trim();
	    }

	    private static int parseInt(String val) {
	        try {
	            return Integer.parseInt(val.trim());
	        } catch (Exception e) {
	            return 0;
	        }
	    }

}
