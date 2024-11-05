package ee.smit.clients;

import ee.smit.clients.api.london.London;
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
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static ee.smit.commons.enums.RequestType.AVAILABLE_TIME;
import static ee.smit.commons.enums.RequestType.BOOKING;

@Slf4j
@Service
public class LondonClient {

    @Autowired
    HttpCall httpCall;

    @Autowired
    LondonProperties londonProperties;

    public London getAvailableTime(LocalDate from, LocalDate until) {
        log.info("getAvailableTime Request from: -> {} ; until: -> {}", from, until);

        London response = London.builder()
                .tireChangeTimesResponse(
                        fromXml(
                                urlExecutorAvailableTime("/available?from=" + from + "&until=" + until), TireChangeTimesResponse.class))
                .build();

        log.info("getAvailableTime Response: -> {}", response);
        return response;
    }

    public London bookTime(UUID uuid, String bookingInfo) {
        log.info("bookTime Request uuid: -> {} ; bookingInfo: -> {}", uuid, bookingInfo);

        String executionUrlCore = "/" + uuid+"/booking";

        London response = London.builder()
                .tireChangeBookingResponse(
                        fromXml(
                                urlExecutor(BOOKING, executionUrlCore, buildBookingBodyXML(bookingInfo)), TireChangeBookingResponse.class))
                .build();
        log.info("bookTime Response: -> {}", response);
        return response;
    }

    public <T> T fromXml(String londonUrlResponse, Class<T> classType) {

        if(Objects.requireNonNull(londonUrlResponse).isEmpty()) {
            throw new InternalServerErrorException(500, "blank london response");
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classType);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(londonUrlResponse));
        } catch (JAXBException ex) {
            throw new InternalServerErrorException(500, ex.getMessage());
        }
    }

    private String urlExecutorAvailableTime(String urlRequest) {
        return urlExecutor(AVAILABLE_TIME, urlRequest, null);
    }

    private String urlExecutor(RequestType requestType, String urlRequest, String requestBody) {
        String URL = londonProperties.getApi().getEndpoint() + londonProperties.getApi().getTirechangepath();
        Response response = null;
        try {
            switch (requestType) {
                case AVAILABLE_TIME -> response = httpCall.get(URL + urlRequest);
                case BOOKING -> response = httpCall.put(URL + urlRequest, requestBody);
            }

            if(Objects.requireNonNull(response).isSuccessful()) {
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

    private String buildBookingBodyXML(String bookingInfo) {
        return "<london.tireChangeBookingRequest>\n" +
                "\t<contactInformation>"+bookingInfo+"</contactInformation>\n" +
                "</london.tireChangeBookingRequest>'";
    }
}