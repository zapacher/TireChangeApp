import ee.smta.api.LondonRequest;
import ee.smta.clients.HttpCall;
import ee.smta.clients.LondonClient;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBException;

@SpringBootTest(classes = LondonClient.class)
@Import({RestTemplate.class, OkHttpClient.class, HttpCall.class})
public class TireChangeTestApplication {

    @Autowired
    LondonClient londonClient;
    @Autowired
    HttpCall httpCall;

//    @Test
//    void test_RequestAvailableTime() throws JAXBException {
//
//        System.out.println(londonClient.getAvailableTime(
//                LondonRequest.builder()
//                        .from("2006-01-02")
//                        .until("2030-01-02")
//                        .build()));
//    }
    @Test
    void test_RequestBooking() throws JAXBException {
        System.out.println(londonClient.bookTime(
                LondonRequest.builder()
                        .uuid("479110e4-5f6c-42fe-9f2c-acd0c4ce9ed0")
                        .bookingInfo("Info")
                        .build()));
//        System.out.println(
//                httpCall.put("http://localhost:9003/api/v1/tire-change-times/760aa683-8436-49c4-a065-02665aad167c/booking", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                        "<london.tireChangeBookingRequest>\n" +
//                        "\t<contactInformation>TSTTTTSTST</contactInformation>\n" +
//                        "</london.tireChangeBookingRequest>")
//
//        );
//    RestTemplate restTemplate = new RestTemplate();
//    String result = restTemplate.execute("http://localhost:9003/api/v1/tire-change-times/760aa683-8436-49c4-a065-02665aad167c/booking",
//            HttpMethod.PUT,null,null,"").;
//    System.out.println(result);
    }
}
