package ee.smta.clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ee.smta.api.RequestType;
import ee.smta.api.manchester.ManchesterRequest;
import ee.smta.api.manchester.ManchesterResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.List;

import static ee.smta.api.RequestType.AVAILABLE_TIME;
import static ee.smta.api.RequestType.BOOKING;

public class ManchesterClient {
    @Autowired
    private HttpCall httpCall;

    private final String baseUrl = "http://localhost:9004/api/v2/tire-change-times";

    public ManchesterResponse getAvailableTime(ManchesterRequest manchesterRequest) {
        return toJsonList(urlExecutor(AVAILABLE_TIME, manchesterRequest));
    }

    public ManchesterResponse bookTime(ManchesterRequest manchesterRequest) {
        return toJson(urlExecutor(BOOKING, manchesterRequest));
    }

    private String urlExecutor(RequestType requestType, ManchesterRequest manchesterRequest) {
        switch (requestType) {
            case AVAILABLE_TIME -> {
                return httpCall.get(baseUrl+"?from="+manchesterRequest.getFrom());
            }
            case BOOKING -> {
                return httpCall.post(baseUrl+"/"+manchesterRequest.getId()+"/booking",
                        "{\"contactInformation\" : \"" + manchesterRequest.getContactInformation()+"\"}");
            }
        }
        return null;
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
