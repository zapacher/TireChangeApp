package ee.smit.clients.api.manchester;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class ManchesterResponse {

    private ErrorResponse errorResponse;

    private String id;

    private String time;

    private boolean booked;

    List<AvailableTime> availableTimes;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class AvailableTime {
        private String id;

        private String time;

        private boolean available;
    }
}
