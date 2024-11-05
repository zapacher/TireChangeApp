package ee.smit.clients.api.london;

import ee.smit.clients.api.Request;
import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class London {
    public TireChangeTimesResponse tireChangeTimesResponse;
    private TireChangeBookingRequest tireChangeBookingRequest;
    private TireChangeBookingResponse tireChangeBookingResponse;
    private ErrorResponse errorResponse;

    @Data
    @Builder
    public static class TireChangeBookingRequest implements Request {
        private LocalDate from;
        private LocalDate until;
        private String bookingInfo;
        private UUID uuid;
    }
}