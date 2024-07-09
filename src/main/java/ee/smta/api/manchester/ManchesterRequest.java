package ee.smta.api.manchester;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManchesterRequest {
    String amount;
    String page;
    String from;
    String id;
    String contactInformation;
}
