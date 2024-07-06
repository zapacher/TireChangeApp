package ee.smta.api.london;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class LondonResponse {
    public TireChangeTimesResponse tireChangeTimesResponse;

    @Data
    @Builder
    public class TireChangeTimesResponse {
        public List<AvailableTime> availableTime;

        @Data
        @Builder
        public class AvailableTime {
            public String uuid;
            public Date time;
        }
    }
}