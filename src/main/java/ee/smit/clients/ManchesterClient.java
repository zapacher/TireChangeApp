package ee.smit.clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ee.smit.commons.enums.RequestType;
import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.ManchesterProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        log.info("{} getAvailableTime Request: -> {}",this.getClass().getName() ,request);

        ManchesterResponse response = toJsonList(urlExecutor(AVAILABLE_TIME, request));

        log.info("{} getAvailableTime Response: -> {}",this.getClass().getName() ,response);
        return response;
    }

    public ManchesterResponse bookTime(ManchesterRequest request) {
        log.info("{} bookTime Request: -> {}",this.getClass().getName() ,request);

        ManchesterResponse response = toJson(urlExecutor(BOOKING, request));

        log.info("{} bookTime Response: -> {}",this.getClass().getName() ,response);
        return response;
    }

    private String urlExecutor(RequestType requestType, ManchesterRequest manchesterRequest) {
        final String URL = manchesterProperties.getApi().getEndpoint() + manchesterProperties.getApi().getTirechangepath();
        Response response = null;
        try {
            switch (requestType) {
                case AVAILABLE_TIME -> response = httpCall.get(URL + "?from=" + manchesterRequest.getFrom());
                case BOOKING -> response = httpCall.post(URL + "/" + manchesterRequest.getId() + "/booking",
                        "{\"contactInformation\" : \"" + manchesterRequest.getContactInformation() + "\"}");
            }

            if(response == null) {
                throw new BadRequestException(500, "Service is currently unreachable");
            }

            if (response.isSuccessful()) {
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
        Type availableTimeListType = new TypeToken<List<ManchesterResponse.AvailableTime>>(){}.getType();
        List<ManchesterResponse.AvailableTime> timeList = gson.fromJson(jsonString, availableTimeListType);

        List<ManchesterResponse.AvailableTime> availableTimes = new ArrayList<>();

        for(ManchesterResponse.AvailableTime availableTime : timeList) {
            if(availableTime.isAvailable()) {
                availableTimes.add(availableTime);
            }
        }

        return ManchesterResponse.builder()
                .availableTimes(availableTimes)
                .build();
    }

    private ManchesterResponse toJson(String jsonString) {
        return gson.fromJson(jsonString, ManchesterResponse.class);
    }

    Gson gson = new Gson();
}
