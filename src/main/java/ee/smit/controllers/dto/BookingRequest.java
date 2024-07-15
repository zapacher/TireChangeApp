package ee.smit.controllers.dto;

import ee.smit.api.Location;
import ee.smit.commons.errors.ErrorResponse;
import ee.smit.controllers.dto.groups.Request;
import lombok.*;

import javax.validation.constraints.NotNull;

@Value
@Builder
@AllArgsConstructor
public class BookingRequest {

    @NotNull(groups = {Request.class})
    String id;

    String bookingTime;

    @NotNull(groups = {Request.class})
    String info;

    Location location;

}
