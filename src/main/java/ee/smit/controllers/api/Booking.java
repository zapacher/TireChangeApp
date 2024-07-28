package ee.smit.controllers.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.smit.commons.enums.Locations;
import ee.smit.controllers.api.groups.Request;
import ee.smit.controllers.api.groups.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {

    @NotNull
    String id;

    @NotNull(groups = {Response.class})
    String bookingTime;

    @NotNull(groups = {Request.class})
    @Min(value = 1)
    String info;

    @NotNull(groups = {Request.class})
    Locations location;

    @NotNull(groups = {Request.class})
    String userTime;

    boolean isBooked;
}
