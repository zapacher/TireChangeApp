package ee.smit.clients.api.london;

import ee.smit.clients.api.Response;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "tireChangeBookingResponse")
public class TireChangeBookingResponse implements Response {
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