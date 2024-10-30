package ee.smit.clients.api.london;

import ee.smit.clients.api.Request;
import ee.smit.clients.api.Response;
import ee.smit.commons.errors.ErrorResponse;
import lombok.Builder;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@XmlRootElement(name = "london")
public class London {

    @XmlElement(name = "tireChangeBookingRequest")
    private TireChangeBookingRequest tireChangeBookingRequest;

    @XmlElement(name = "tireChangeBookingResponse")
    private TireChangeBookingResponse tireChangeBookingResponse;

    @XmlElement(name = "tireChangeTimesResponse")
    private TireChangeTimesResponse tireChangeTimesResponse;

    @XmlElement(name = "errorResponse")
    private ErrorResponse errorResponse;

    @Data
    @Builder
    public static class TireChangeBookingRequest implements Request {
        @XmlElement(name = "from")
        LocalDate from;

        @XmlElement(name = "until")
        LocalDate until;

        @XmlElement(name = "bookingInfo")
        String bookingInfo;

        @XmlElement(name = "uuid")
        UUID uuid;
    }

    @Data
    public static class TireChangeBookingResponse implements Response {
        @XmlElement(name = "uuid")
        UUID uuid;

        @XmlElement(name = "time")
        String time;
    }

    @Data
    public static class TireChangeTimesResponse implements Response {
        @XmlElement(name = "availableTime")
        List<AvailableTime> availableTime = new ArrayList<>();

        @Data
        public static class AvailableTime {
            @XmlElement(name = "uuid")
            UUID uuid;

            @XmlElement(name = "time")
            String time;
        }
    }
}