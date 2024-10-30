package ee.smit.clients.api.london;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@XmlElement(name = "tireChangeTimesResponse")
public class TireChangeTimesResponse {
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