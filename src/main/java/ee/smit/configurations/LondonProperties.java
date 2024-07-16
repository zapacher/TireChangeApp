package ee.smit.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "london")
public class LondonProperties {
    String endpoint;
    String tirechangepath;
    String info;
}
