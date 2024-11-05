package ee.smit.clients.api.london;

import ee.smit.clients.api.Response;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "tireChangeTimesResponse")
public class TireChangeTimesResponse implements Response {
    private List<AvailableTime> availableTime;

    @XmlElement(name = "availableTime")
    public List<AvailableTime> getAvailableTime() {
        return availableTime;
    }

    @Data
    public static class AvailableTime {
        private String uuid;
        private String time;

        @XmlElement(name = "uuid")
        public String getUuid() {
            return uuid;
        }

        @XmlElement(name = "time")
        public String getTime() {
            return time;
        }
    }
}