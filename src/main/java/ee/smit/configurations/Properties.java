package ee.smit.configurations;

import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class Properties {
    @Autowired
    LondonProperties londonProperties;
    @Autowired
    ManchesterProperties manchesterProperties;

    public HashMap<Locations, List<VehicleTypes>> getAvailableLocations() {
        HashMap<Locations, List<VehicleTypes>> response = new HashMap<>();

        if(londonProperties.isAvailable()) {
            response.put(londonProperties.getLocation(), londonProperties.getVehicleTypes());
        }

        if(manchesterProperties.isAvailable()) {
            response.put(manchesterProperties.getLocation(), manchesterProperties.getVehicleTypes());
        }
        return response;
    }
}
