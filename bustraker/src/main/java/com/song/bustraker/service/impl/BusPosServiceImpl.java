package com.example.bustraker.service;

import com.example.bustraker.dto.BusPosDto;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusPosServiceImpl implements BusPosService {

    @Override
    public List<BusPosDto> getBusPositions(String routeId) {
        List<BusPosDto> busList = new ArrayList<>();
        try {
            // ✅ API 호출 URL
            String urlStr = "http://apis.data.go.kr/1234567/BusPos/getBusPos?routeId=" + routeId + "&serviceKey=발급받은키";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // ✅ XML 파싱 준비
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("itemList");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    BusPosDto bus = new BusPosDto();

                    bus.setArrTime(getTagValue("ARR_TIME", element));
                    bus.setBusNodeId(getTagValue("BUS_NODE_ID", element));
                    bus.setBusStopId(getTagValue("BUS_STOP_ID", element));

                    // ✅ DIR 변환 (0 = 상행, 1 = 하행)
                    String dirStr = getTagValue("DIR", element);
                    int dirVal = (dirStr != null && !dirStr.isEmpty()) ? Integer.parseInt(dirStr) : -1;
                    bus.setDir(dirVal);

                    bus.setEvtCd(getTagValue("EVT_CD", element));
                    bus.setGpsLati(getTagValue("GPS_LATI", element));
                    bus.setGpsLong(getTagValue("GPS_LONG", element));
                    bus.setPlateNo(getTagValue("PLATE_NO", element));
                    bus.setRouteCd(getTagValue("ROUTE_CD", element));
                    bus.setStreDt(getTagValue("STRE_DT", element));
                    bus.setTotalDist(getTagValue("TOTAL_DIST", element));

                    // ✅ UD_TYPE 변환
                    String udTypeStr = getTagValue("UD_TYPE", element);
                    int udTypeVal = (udTypeStr != null && !udTypeStr.isEmpty()) ? Integer.parseInt(udTypeStr) : -1;
                    bus.setUdType(udTypeVal);

                    busList.add(bus);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return busList;
    }

    // ✅ XML 태그 값 꺼내기
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            NodeList childNodes = nodeList.item(0).getChildNodes();
            if (childNodes.getLength() > 0) {
                return childNodes.item(0).getNodeValue();
            }
        }
        return null;
    }
}
