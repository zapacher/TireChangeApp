package ee.smit.clients.api.london;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LondonRequest {
    LocalDate from;
    LocalDate until;
    String bookingInfo;
    UUID uuid;
}
