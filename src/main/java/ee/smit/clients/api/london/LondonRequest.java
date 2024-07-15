package ee.smit.clients.api.london;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LondonRequest {
    String from;
    String until;
    String bookingInfo;
    String uuid;
}
