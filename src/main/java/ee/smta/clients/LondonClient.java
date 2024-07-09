package ee.smta.clients;

import ee.smta.api.RequestType;
import ee.smta.api.error.BadRequestException;
import ee.smta.api.error.InternalServerErrorException;
import ee.smta.api.london.LondonRequest;
import ee.smta.api.london.LondonResponse;
import ee.smta.api.london.TireChangeBookingResponse;
import ee.smta.api.london.TireChangeTimesResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.UUID;

import static ee.smta.api.RequestType.AVAILABLE_TIME;
import static ee.smta.api.RequestType.BOOKING;

public class LondonClient {

    @Autowired
    private HttpCall httpCall;

    private final String baseUrl = "http://localhost:9003/api/v1/tire-change-times/";

    public LondonResponse getAvailableTime(LondonRequest londonRequest) {
        return LondonResponse.builder()
                .tireChangeTimesResponse(fromXml(TireChangeTimesResponse.class, AVAILABLE_TIME, londonRequest))
                .build();
    }

    public LondonResponse bookTime(LondonRequest londonRequest) {
        /*
         !!WARNING!! if info for uuid is equal as the booked one, it will be successfully booked. For that,
          reRequest of available booking time before execution call.
         */
        checkAvailability(londonRequest);
        return LondonResponse.builder()
                .tireChangeBookingResponse(fromXml(TireChangeBookingResponse.class, BOOKING, londonRequest))
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

    private void checkAvailability(LondonRequest londonRequest) {
        LondonResponse londonResponse = getAvailableTime(LondonRequest.builder()
                .from("2006-01-02")
                .until("2030-01-02")
                .build());
        boolean isAvailable = false;
        for(TireChangeTimesResponse.AvailableTime availableTime: londonResponse.getTireChangeTimesResponse().getAvailableTime()) {
            if(availableTime.getUuid().equals(londonRequest.getUuid())) {
                isAvailable = true;
                break;
            }
        }
        if(!isAvailable) {
            try {
                UUID.fromString(londonRequest.getUuid());
            } catch (IllegalArgumentException ignore) {
                throw new InternalServerErrorException();
            }
            throw new BadRequestException(422, "This time is already booked");
        }
    }


    public <T> T fromXml(Class<T> responseClass, RequestType requestType, LondonRequest londonRequest) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(responseClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(urlExecutor(requestType, londonRequest)));
        } catch (JAXBException ex) {
            throw new InternalServerErrorException(500, ex.getMessage());
        }
    }

    private String buildBookingBodyXML(String bookingInfo) {
        return "<london.tireChangeBookingRequest>\n" +
                "\t<contactInformation>"+bookingInfo+"</contactInformation>\n" +
                "</london.tireChangeBookingRequest>'";
    }
}
