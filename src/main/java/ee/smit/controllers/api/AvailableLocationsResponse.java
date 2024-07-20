package ee.smit.controllers.api;

import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableLocationsResponse {
    HashMap<Locations, List<VehicleTypes>> availableLocationsResponse = new HashMap<>();

}
