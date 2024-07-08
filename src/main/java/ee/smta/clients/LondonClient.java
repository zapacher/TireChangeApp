package ee.smta.clients;

import ee.smta.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static ee.smta.api.RequestType.AVAILABLE_TIME;
import static ee.smta.api.RequestType.BOOKING;

public class LondonClient {

    @Autowired
    HttpCall httpCall;

    private String baseUrl = "http://localhost:9003/api/v1/tire-change-times/";

    public LondonResponse getAvailableTime(LondonRequest londonRequest) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(TireChangeTimesResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TireChangeTimesResponse response = (TireChangeTimesResponse) unmarshaller.unmarshal(
                new StringReader(urlExecutor(AVAILABLE_TIME, londonRequest)));
        return LondonResponse.builder()
                .tireChangeTimesResponse(response)
                .build();
    }

    public LondonResponse bookTime(LondonRequest londonRequest) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TireChangeBookingResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TireChangeBookingResponse response = (TireChangeBookingResponse) unmarshaller.unmarshal(
                new StringReader(urlExecutor(BOOKING, londonRequest)));
        return LondonResponse.builder()
                .tireChangeBookingResponse(response)
                .build();
    }

    private String urlExecutor(RequestType requestType, LondonRequest londonRequest) {
        switch (requestType) {
            case AVAILABLE_TIME -> {
                return httpCall.get(baseUrl + "available?from=" + londonRequest.getFrom() + "&until=" + londonRequest.getUntil(), null);
            }
            case BOOKING -> {
                return httpCall.put(baseUrl+londonRequest.getUuid()+"/booking", buildBookingBody(londonRequest.getBookingInfo()));
            }
        }
        return null;
    }

    private String buildBookingBody(String bookingInfo) {
        return "<london.tireChangeBookingRequest>\n" +
                "\t<contactInformation>"+bookingInfo+"</contactInformation>\n" +
                "</london.tireChangeBookingRequest>'";
    }
}
