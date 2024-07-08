package ee.smta.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LondonRequest {
    String from;
    String until;
    String bookingInfo;
    String uuid;
}
