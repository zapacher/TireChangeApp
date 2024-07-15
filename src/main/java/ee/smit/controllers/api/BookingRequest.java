package ee.smit.controllers.api;

import ee.smit.api.Location;
import ee.smit.controllers.api.groups.Request;
import lombok.*;

import javax.validation.constraints.NotNull;

@Value
@Builder
@AllArgsConstructor
public class BookingRequest {

    @NotNull(groups = {Request.class})
    String id;

    @NotNull(groups = {Request.class})
    String bookingTime;

    @NotNull(groups = {Request.class})
    String info;

    @NotNull(groups = {Request.class})
    Location location;

}
