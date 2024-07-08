package ee.smta.api;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Data
public class TireChangeBookingResponse {
    String uuid;
    String time;
}
