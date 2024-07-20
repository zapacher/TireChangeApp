package ee.smit.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "environment.london")
public class LondonProperties {
    String endpoint;
    String tirechangepath;
    String info;
}
