package ee.smit.configurations;

import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

import static ee.smit.commons.enums.Locations.LONDON;

@Data
@Component
@ConfigurationProperties(prefix = "environment.london")
public class LondonProperties {
    String address;
    Api api;
    boolean available;
    Locations location;
    List<VehicleTypes> vehicleTypes;

    @Data
    public static class Api {
        String endpoint;
        String tirechangepath;
    }
}
