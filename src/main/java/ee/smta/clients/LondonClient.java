package ee.smta.clients;

import ee.smta.api.RequestType;
import ee.smta.api.error.BadRequestException;
import ee.smta.api.london.LondonRequest;
import ee.smta.api.london.LondonResponse;
import ee.smta.api.london.TireChangeBookingResponse;
import ee.smta.api.london.TireChangeTimesResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static ee.smta.api.RequestType.AVAILABLE_TIME;
import static ee.smta.api.RequestType.BOOKING;

public class LondonClient {

    @Autowired
    private HttpCall httpCall;

    private final String baseUrl = "http://localhost:9003/api/v1/tire-change-times/";

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
        //!!WARNING!! if info for uuid is equal as the booked one, it will be successfully booked. For that,
        // reRequest of available booking time before execution call.
        checkAvailability(londonRequest);

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
                return httpCall.get(baseUrl + "available?from=" + londonRequest.getFrom() + "&until=" + londonRequest.getUntil());
            }
            case BOOKING -> {
                return httpCall.put(baseUrl+londonRequest.getUuid()+"/booking",
                        buildBookingBodyXML(londonRequest.getBookingInfo()));
            }
        }
        return null;
    }

    private void checkAvailability(LondonRequest londonRequest) throws JAXBException {
        LondonResponse londonResponse = getAvailableTime(LondonRequest.builder()
                .from("2006-01-02")
                .until("2030-01-02")
                .build());
        boolean isAvailable = false;
        for(TireChangeTimesResponse.AvailableTime availableTime: londonResponse.getTireChangeTimesResponse().getAvailableTime()) {
            if(availableTime.equals(londonRequest.getUuid())) {
                isAvailable = true;
            }
        }
        if(!isAvailable) {
            throw new BadRequestException(422, "This time is already booked");
        }
    }

    private String buildBookingBodyXML(String bookingInfo) {
        return "<london.tireChangeBookingRequest>\n" +
                "\t<contactInformation>"+bookingInfo+"</contactInformation>\n" +
                "</london.tireChangeBookingRequest>'";
    }
}
