package ee.smit.controllers.api;

import ee.smit.commons.errors.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class BookingResponse {

    String bookingTime;

    String id;

    boolean isBooked;

    ErrorResponse errorResponse;
}