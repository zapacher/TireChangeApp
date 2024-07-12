package ee.smta.clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ee.smta.api.RequestType;
import ee.smta.api.manchester.ManchesterRequest;
import ee.smta.api.manchester.ManchesterResponse;
import ee.smta.common.HttpCall;
import ee.smta.common.error.BadRequestException;
import ee.smta.common.error.InternalServerErrorException;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import static ee.smta.api.RequestType.AVAILABLE_TIME;
import static ee.smta.api.RequestType.BOOKING;

public class ManchesterClient {
    @Autowired
    HttpCall httpCall;

    private static final String BASE_URL = "http://localhost:9004/api/v2/tire-change-times";

    public ManchesterResponse getAvailableTime(ManchesterRequest manchesterRequest) {
        return toJsonList(urlExecutor(AVAILABLE_TIME, manchesterRequest));
    }

    public ManchesterResponse bookTime(ManchesterRequest manchesterRequest) {
        return toJson(urlExecutor(BOOKING, manchesterRequest));
    }

    private String urlExecutor(RequestType requestType, ManchesterRequest manchesterRequest) {
        Response response = null;
        try {
            switch (requestType) {
                case AVAILABLE_TIME -> response = httpCall.get(BASE_URL + "?from=" + manchesterRequest.getFrom());
                case BOOKING -> response = httpCall.post(BASE_URL + "/" + manchesterRequest.getId() + "/booking",
                        "{\"contactInformation\" : \"" + manchesterRequest.getContactInformation() + "\"}");
            }
            if (response.isSuccessful()) {
                if (Objects.requireNonNull(response.body()).string().isEmpty()) {
                    return response.body().string();
                }
                throw new InternalServerErrorException();
            } else {
                switch (response.code()) {
                    case 400 -> throw new BadRequestException(400, "Bad Request");
                    case 422 -> throw new BadRequestException(422, "This time is already booked");
                    default -> throw new InternalServerErrorException();
                }
            }
        } catch(IOException ignore){
            throw new InternalServerErrorException();
        }
    }

    private ManchesterResponse toJsonList(String jsonString) {
        Type availableTimeListType = new TypeToken<List<ManchesterResponse.AvailableTime>>(){}.getType();
        List<ManchesterResponse.AvailableTime> availableTimes = gson.fromJson(jsonString, availableTimeListType);

        return ManchesterResponse.builder()
                .availableTimes(availableTimes)
                .build();
    }

    private ManchesterResponse toJson(String jsonString) {
        return gson.fromJson(jsonString, ManchesterResponse.class);
    }

    Gson gson = new Gson();
}
