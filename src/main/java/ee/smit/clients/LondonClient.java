package ee.smit.clients;

import ee.smit.api.RequestType;
import ee.smit.api.london.LondonRequest;
import ee.smit.api.london.LondonResponse;
import ee.smit.api.london.TireChangeBookingResponse;
import ee.smit.api.london.TireChangeTimesResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.ClientEndpoint;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.UUID;

import static ee.smit.api.RequestType.AVAILABLE_TIME;
import static ee.smit.api.RequestType.BOOKING;

@Service
public class LondonClient {

    @Autowired
    HttpCall httpCall;

    private static final String BASE_URL = "http://localhost:9003/api/v1/tire-change-times/";

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
        Response response = null;
        try {
            switch (requestType) {
                case AVAILABLE_TIME -> response =  httpCall.get(BASE_URL + "available?from=" + londonRequest.getFrom()
                        + "&until=" + londonRequest.getUntil());
                case BOOKING -> response = httpCall.put(BASE_URL +londonRequest.getUuid()+"/booking",
                        buildBookingBodyXML(londonRequest.getBookingInfo()));
            }
            if(response.isSuccessful()) {
                return response.body().string();
            } else {
                switch(response.code()) {
                    case 400 -> throw new BadRequestException(400, "Bad Request");
                    case 422 -> throw new BadRequestException(422, "This time is already booked");
                    default -> throw new InternalServerErrorException();
                }
            }
        } catch (IOException ignore) {
            throw new InternalServerErrorException();
        }
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
        String urlResponse = urlExecutor(requestType, londonRequest);
        if(Objects.requireNonNull(urlResponse).isEmpty()) {
            throw new InternalServerErrorException(500, "blank london response");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(responseClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(urlResponse));
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
