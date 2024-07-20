package ee.smit.configurations;

import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
