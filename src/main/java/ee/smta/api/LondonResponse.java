package ee.smta.api;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class LondonResponse {
    TireChangeTimesResponse tireChangeTimesResponse;
    TireChangeBookingResponse tireChangeBookingResponse;
}
