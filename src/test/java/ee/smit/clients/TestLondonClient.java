package ee.smit.clients;

import ee.smit.api.london.LondonRequest;
import ee.smit.api.london.LondonResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LondonClient.class)
@Import({RestTemplate.class, OkHttpClient.class, HttpCall.class})
public class TestLondonClient {

    @Autowired
    LondonClient londonClient;

    LondonResponse londonResponse;

    @Test
    void test_LondonRequestAvailableTime() {
        LondonResponse londonResponse = londonClient.getAvailableTime(
                LondonRequest.builder()
                        .from("2006-01-02")
                        .until("2030-01-02")
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

        String londonBookingTestUuid = londonResponse.getTireChangeTimesResponse().getAvailableTime().get(0).getUuid();

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

        String londonBookingTestUuid = londonResponse.getTireChangeTimesResponse().getAvailableTime().get(0).getUuid();
        String info = "TEST_INFO";
        dummyCallLondon(londonBookingTestUuid, info);
        assertAll(
                () -> assertThrows(BadRequestException.class,
                        () -> londonClient.bookTime(
                                LondonRequest.builder()
                                        .uuid(londonBookingTestUuid)
                                        .bookingInfo(info)
                                        .build())
                ),
                () -> assertThrows(BadRequestException.class,
                        () -> londonClient.bookTime(
                                LondonRequest.builder()
                                        .uuid(londonBookingTestUuid)
                                        .bookingInfo(info+1)
                                        .build())
                ),
                () -> assertThrows(InternalServerErrorException.class,
                        () -> londonClient.bookTime(
                                LondonRequest.builder()
                                        .uuid("null")
                                        .bookingInfo("null")
                                        .build())
                )
        );
    }

    private void dummyCallLondon(String id, String info) {
        try {
            londonClient.bookTime(
                    LondonRequest.builder()
                            .uuid(id)
                            .bookingInfo(info)
                            .build());
        } catch (BadRequestException ignore) {}
    }
}
