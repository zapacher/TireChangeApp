package ee.smit.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "environment.manchester")
public class ManchesterProperties {
    String endpoint;
    String tirechangepath;
    String info;
}
