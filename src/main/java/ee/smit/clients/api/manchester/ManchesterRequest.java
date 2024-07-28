package ee.smit.clients.api.manchester;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManchesterRequest {
    String amount;
    String page;
    LocalDate from;
    String id;
    String contactInformation;
}
