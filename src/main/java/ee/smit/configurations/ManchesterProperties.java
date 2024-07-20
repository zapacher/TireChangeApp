package ee.smit.configurations;

import ee.smit.commons.enums.Locations;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static ee.smit.commons.enums.Locations.MANCHESTER;

@Data
@ConfigurationProperties(prefix = "environment.manchester")
public class ManchesterProperties {
    String address;
    Api api;
    boolean available;
    Locations location = MANCHESTER;

    @Data
    public static class Api {
        String endpoint;
        String tirechangepath;
    }
}
