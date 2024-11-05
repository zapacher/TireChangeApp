package ee.smit.clients.api.london;

import ee.smit.clients.api.Request;
import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/*
    Documentation for London has error in XML Models
     <london> - isn't necessary element in responses
 */
@Data
@Builder
public class LondonDTO {
    public TireChangeTimesResponse tireChangeTimesResponse;
    private TireChangeBookingRequest tireChangeBookingRequest;
    private TireChangeBookingResponse tireChangeBookingResponse;
    private ErrorResponse errorResponse;

    @Data
    @Builder
    public static class TireChangeBookingRequest implements Request {
        private LocalDate from;
        private LocalDate until;
        private String bookingInfo;
        private UUID uuid;

        public String getAvailableTimeRequestUrlPath() {
            return "/available?from=" + from + "&until=" + until;
        }

        public String getBookingRequestUrlPath() {
            return "/" + uuid + "/booking";
        }

        public String getBookingRequestBody() {
            return "<london.tireChangeBookingRequest>\n" +
                    "\t<contactInformation>"+bookingInfo+"</contactInformation>\n" +
                    "</london.tireChangeBookingRequest>'";
        }
    }
}