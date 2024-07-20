package ee.smit.controllers.api;

import ee.smit.api.Location;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class AvailableTimeRequest {
    @NotNull
    Location location;
}
