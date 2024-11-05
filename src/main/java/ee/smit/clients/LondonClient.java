package ee.smit.clients;

import ee.smit.clients.api.london.LondonDTO;
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

import java.io.IOException;
import java.util.Objects;

import static ee.smit.commons.XmParser.fromXml;
import static ee.smit.commons.enums.RequestType.AVAILABLE_TIME;
import static ee.smit.commons.enums.RequestType.BOOKING;

@Slf4j
@Service
public class LondonClient {

    @Autowired
    HttpCall httpCall;

    @Autowired
    LondonProperties londonProperties;

    public LondonDTO getAvailableTime(LondonDTO londonDTO) {
        log.info("getAvailableTime Request ()-> {}", londonDTO.getTireChangeBookingRequest());

        LondonDTO response = LondonDTO.builder()
                .tireChangeTimesResponse(
                        fromXml(
                                urlExecutorAvailableTime(londonDTO.getTireChangeBookingRequest().getAvailableTimeRequestUrlPath()),
                                TireChangeTimesResponse.class))
                .build();

        log.info("getAvailableTime Response: -> {}", response);
        return response;
    }

    public LondonDTO bookTime(LondonDTO londonDTO) {
        log.info("bookTime Request: -> {}", londonDTO);

        LondonDTO.TireChangeBookingRequest request = londonDTO.getTireChangeBookingRequest();
        LondonDTO response = LondonDTO.builder()
                .tireChangeBookingResponse(
                        fromXml(
                                urlExecutor(BOOKING, request.getBookingRequestUrlPath(), request.getBookingRequestBody()),
                                TireChangeBookingResponse.class))
                .build();
        log.info("bookTime Response: -> {}", response);
        return response;
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
}