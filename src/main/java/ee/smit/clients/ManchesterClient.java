package ee.smit.clients;

import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.ManchesterProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ee.smit.commons.JsonParser.fromJsonList;
import static ee.smit.commons.JsonParser.fromJson;
import static ee.smit.commons.enums.RequestType.AVAILABLE_TIME;
import static ee.smit.commons.enums.RequestType.BOOKING;

@Slf4j
@Service
public class ManchesterClient {

    @Autowired
    HttpCall httpCall;

    @Autowired
    ManchesterProperties manchesterProperties;

    public ManchesterResponse getAvailableTime(ManchesterRequest request) {
        log.info("getAvailableTime Request: -> {}", request);

        ManchesterResponse response = toJsonList(urlExecutor(AVAILABLE_TIME, request));

        log.info("getAvailableTime Response: -> {}", response);
        return response;
    }

    public ManchesterResponse bookTime(ManchesterRequest request) {
        log.info("bookTime Request: -> {}", request);

        ManchesterResponse response = fromJson(urlExecutor(BOOKING, request), ManchesterResponse.class);

        log.info("bookTime Response: -> {}", response);
        return response;
    }

    private String urlExecutor(RequestType requestType, ManchesterRequest manchesterRequest) {
        final String URL = manchesterProperties.getApi().getEndpoint() + manchesterProperties.getApi().getTirechangepath();
        Response response = null;
        try {
            switch (requestType) {
                case AVAILABLE_TIME -> response = httpCall.get(URL + manchesterRequest.getFromUrlPath());
                case BOOKING -> response = httpCall.post(URL + manchesterRequest.getBookingUrlPath(),
                        manchesterRequest.getContactInformationOnlyBody());
            }
            if(response == null) {
                throw new BadRequestException(500, "Service is currently unreachable");
            }

            if (Objects.requireNonNull(response).isSuccessful()) {
                return response.body().string();
            } else {
                switch (response.code()) {
                    case 400 -> throw new BadRequestException(400, "Bad Request");
                    case 422 -> throw new BadRequestException(422, "This time is already booked");
                    default -> throw new InternalServerErrorException();
                }
            }
        } catch(IOException ignore) {
            throw new InternalServerErrorException();
        }
    }

    private ManchesterResponse toJsonList(String jsonString) {
        List<ManchesterResponse.AvailableTime> availableTimes = new ArrayList<>();

        for(ManchesterResponse.AvailableTime availableTime : fromJsonList(jsonString, ManchesterResponse.AvailableTime.class)) {
            if(availableTime.isAvailable()) {
                availableTimes.add(availableTime);
            }
        }

        return ManchesterResponse.builder()
                .availableTimes(availableTimes)
                .build();
    }
}
