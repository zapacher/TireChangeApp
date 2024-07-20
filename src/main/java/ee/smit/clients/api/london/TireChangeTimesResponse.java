package ee.smit.clients.api.london;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement
@Data
public class TireChangeTimesResponse {
    List<AvailableTime> availableTime = new ArrayList<>();

    @Data
    public static class AvailableTime {
        UUID uuid;
        String time;
    }
}