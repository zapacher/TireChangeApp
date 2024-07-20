package ee.smit.controllers.api;

import ee.smit.commons.enums.VehicleTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeResponse {
    List<VehicleTypes> vehicleTypes = new ArrayList<>();
    List<AvailableTime> availableTimeList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTime {
        String id;
        String availableTime;
    }
}
