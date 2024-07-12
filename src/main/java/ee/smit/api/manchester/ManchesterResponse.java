package ee.smit.api.manchester;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class ManchesterResponse {
    private String id;

    private String time;

    private String available;

    List<AvailableTime> availableTimes;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class AvailableTime {
        private String id;

        private String time;

        private String available;
    }
}
