package ee.smit.api.london;

import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LondonResponse {
    ErrorResponse error;
    TireChangeTimesResponse tireChangeTimesResponse;
    TireChangeBookingResponse tireChangeBookingResponse;
}
