package ee.smit.controllers.dto;

import ee.smit.commons.errors.ErrorResponse;
import lombok.*;

import javax.validation.constraints.NotNull;

@Value
@Builder
@AllArgsConstructor
public class BookingDto {

    @NotNull(groups = {Request.class})
    String id;

    String bookingTime;

    @NotNull(groups = {Request.class})
    String info;

    boolean isBooked;

    ErrorResponse errorResponse;
}
