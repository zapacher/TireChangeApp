package ee.smit.configurations;

import ee.smit.commons.enums.Locations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Properties {
    @Autowired
    LondonProperties londonProperties;
    @Autowired
    ManchesterProperties manchesterProperties;

    public List<Locations> getAvailableServices() {
        List<Locations> servicesList = new ArrayList<>();

        if(londonProperties.isAvailable()) {
            servicesList.add(londonProperties.getLocation());
        }

        if(manchesterProperties.isAvailable()) {
            servicesList.add(manchesterProperties.getLocation());
        }
        return servicesList;
    }
}
