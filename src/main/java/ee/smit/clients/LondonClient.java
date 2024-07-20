package ee.smit.clients;

import ee.smit.clients.api.london.LondonRequest;
import ee.smit.clients.api.london.LondonResponse;
import ee.smit.clients.api.london.TireChangeBookingResponse;
import ee.smit.clients.api.london.TireChangeTimesResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.LondonProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

import static ee.smit.commons.enums.RequestType.AVAILABLE_TIME;
import static ee.smit.commons.enums.RequestType.BOOKING;
@Slf4j
@Service
public class LondonClient {

    @Autowired
    HttpCall httpCall;

    @Autowired
    LondonProperties londonProperties;

    public LondonResponse getAvailableTime(LondonRequest request) {
        log.info("{} getAvailableTime Request: -> {}", this.getClass().getName(), request);

        LondonResponse response = LondonResponse.builder()
                .tireChangeTimesResponse(fromXml(TireChangeTimesResponse.class, AVAILABLE_TIME, request))
                .build();

        log.info("{} getAvailableTime Response: -> {}", this.getClass().getName(), response);
        return response;
    }

    public LondonResponse bookTime(LondonRequest request) {
        log.info("{} bookTime Request: -> {}", this.getClass().getName(), request);
        /*
         !!WARNING!! if info for uuid is equal as the booked one, it will be successfully booked. For that,
          reRequest of available booking time before execution call.
         */
        checkAvailability(request);
        LondonResponse response = LondonResponse.builder()
                .tireChangeBookingResponse(fromXml(TireChangeBookingResponse.class, BOOKING, request))
                .build();

        log.info("{} bookTime Response: -> {}", this.getClass().getName(), response);
        return response;
    }

    private String urlExecutor(RequestType requestType, LondonRequest londonRequest) {
        String URL = londonProperties.getApi().getEndpoint() + londonProperties.getApi().getTirechangepath();
        Response response = null;
        try {
            switch (requestType) {
                case AVAILABLE_TIME -> response =  httpCall.get(URL + "/available?from=" + londonRequest.getFrom()
                        + "&until=" + londonRequest.getUntil());
                case BOOKING -> response = httpCall.put(URL + "/" + londonRequest.getUuid()+"/booking",
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

        if(londonResponse.getTireChangeTimesResponse().getAvailableTime()
                .stream()
                .noneMatch(availableTime -> availableTime.getUuid().equals(londonRequest.getUuid()))) {
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
