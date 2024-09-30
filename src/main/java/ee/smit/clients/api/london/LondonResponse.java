package ee.smit.clients.api.london;

import ee.smit.clients.api.Response;
import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LondonResponse implements Response {
    ErrorResponse errorResponse;
    TireChangeTimesResponse tireChangeTimesResponse;
    TireChangeBookingResponse tireChangeBookingResponse;
}
