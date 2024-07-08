package ee.smta.api;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@Data
public class TireChangeTimesResponse {
    List<AvailableTime> availableTime = new ArrayList<>();

    @Data
    public static class AvailableTime {
        String uuid;
        String time;
    }
}