package ee.smit.clients;

import ee.smit.clients.api.london.LondonRequest;
import ee.smit.clients.api.london.LondonResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.configurations.LondonProperties;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnableConfigurationProperties(LondonProperties.class)
@SpringBootTest(classes = LondonClient.class)
@Import({RestTemplate.class, OkHttpClient.class, HttpCall.class})
public class TestLondonClient {

    @Autowired
    LondonClient londonClient;
    @Autowired
    static LondonProperties londonProperties;

    LondonResponse londonResponse;

    @BeforeAll
    static void beforeALl() {
        if(System.getenv("MAVEN_PROJECTBASEDIR")!=null) {
            londonProperties.getApi().setEndpoint("http://172.17.0.1:9003");
        }
    }

    @Test
    void test_LondonRequestAvailableTime() {
        LondonResponse londonResponse = londonClient.getAvailableTime(
                LondonRequest.builder()
                        .from(LocalDate.now())
                        .until(LocalDate.now().plusMonths(5))
                        .build());

        assertAll(
                () -> assertNotNull(londonResponse.getTireChangeTimesResponse(), "london available full response"),
                () -> assertNotNull(londonResponse.getTireChangeTimesResponse().getAvailableTime(),"london available full response list")
        );

        this.londonResponse = londonResponse;
    }

    @Test
    void test_LondonRequestBooking() {
        test_LondonRequestAvailableTime();

        UUID londonBookingTestUuid = londonResponse.getTireChangeTimesResponse().getAvailableTime().get(0).getUuid();

        LondonResponse londonResponse = londonClient.bookTime(
                LondonRequest.builder()
                        .uuid(londonBookingTestUuid)
                        .bookingInfo("Info")
                        .build());

        assertAll(
                () -> assertNotNull(londonResponse.getTireChangeBookingResponse(),"london booking response"),
                () -> assertNotNull(londonResponse.getTireChangeBookingResponse().getTime(), "london booking response time"),
                () -> assertNotNull(londonResponse.getTireChangeBookingResponse().getUuid(), "london booking response uuid")
        );
    }

    @Test
    void test_londonBookingError() {
        test_LondonRequestAvailableTime();

        UUID londonBookingTestUuid = londonResponse.getTireChangeTimesResponse().getAvailableTime().get(0).getUuid();
        String info = "TEST_INFO";
        dummyBookLondon(londonBookingTestUuid, info);
        assertAll(
                () -> assertThrows(BadRequestException.class,
                        () -> londonClient.bookTime(
                                LondonRequest.builder()
                                        .bookingInfo(info)
                                        .build())
                ),
                () -> assertThrows(BadRequestException.class,
                        () -> londonClient.bookTime(
                                LondonRequest.builder()
                                        .uuid(londonBookingTestUuid)
                                        .build())
                )
        );
    }

    private void dummyBookLondon(UUID id, String info) {
        try {
            londonClient.bookTime(
                    LondonRequest.builder()
                            .uuid(id)
                            .bookingInfo(info)
                            .build());
        } catch (BadRequestException ignore) {}
    }
}
