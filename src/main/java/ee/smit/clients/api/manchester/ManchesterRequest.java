package ee.smit.clients.api.manchester;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.smit.clients.api.Request;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManchesterRequest implements Request {
    String amount;
    String page;
    LocalDate from;
    String id;
    String contactInformation;

    public String getFromUrlPath() {
        String fromPath = "?from=";
        return fromPath+from;
    }

    public String getBookingUrlPath() {
        String bookingPath = "/booking";
        return "/" + this.id + bookingPath;
    }

    public String getContactInformationOnlyBody() {
        return "{\"contactInformation\" : \"" + contactInformation + "\"}";
    }
}
