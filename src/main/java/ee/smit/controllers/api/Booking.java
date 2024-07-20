package ee.smit.controllers.api;

import ee.smit.commons.enums.Location;
import ee.smit.commons.errors.ErrorResponse;
import ee.smit.controllers.api.groups.Request;
import ee.smit.controllers.api.groups.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class Booking {

    @NotNull
    String id;

    @NotNull(groups = {Response.class})
    String bookingTime;

    @NotNull(groups = {Request.class})
    String info;

    @NotNull(groups = {Request.class})
    Location location;

    boolean isBooked;

    ErrorResponse errorResponse;
}
