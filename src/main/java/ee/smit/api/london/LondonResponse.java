package ee.smit.api.london;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LondonResponse {
    TireChangeTimesResponse tireChangeTimesResponse;
    TireChangeBookingResponse tireChangeBookingResponse;
}
