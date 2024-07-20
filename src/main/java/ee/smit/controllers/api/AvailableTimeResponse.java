package ee.smit.controllers.api;

import ee.smit.commons.enums.CarTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeResponse {
    List<CarTypes> carTypes = new ArrayList<>();
    List<AvailableTime> availableTimeList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTime {
        String id;
        String availableTime;
    }
}
