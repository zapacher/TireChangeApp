package ee.smit.clients.api.london;

import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LondonResponse {
    ErrorResponse errorResponse;
    TireChangeTimesResponse tireChangeTimesResponse;
    TireChangeBookingResponse tireChangeBookingResponse;
}
