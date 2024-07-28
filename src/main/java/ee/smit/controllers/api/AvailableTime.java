package ee.smit.controllers.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import ee.smit.controllers.api.groups.Request;
import ee.smit.controllers.api.groups.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailableTime {
    @NotNull(groups = Request.class)
    String userTime;

    @NotNull(groups = Response.class)
    String address;

    @NotNull
    Locations location;

    @NotNull(groups = Response.class)
    List<VehicleTypes> vehicleTypes = new ArrayList<>();

    @NotNull(groups = Response.class)
    List<AvailableTimeList> availableTimeList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTimeList {
        @NotNull(groups = Response.class)
        String id;

        @NotNull(groups = Response.class)
        String availableTime;
    }
}
