import ee.smta.api.error.BadRequestException;
import ee.smta.api.london.LondonRequest;
import ee.smta.api.london.LondonResponse;
import ee.smta.api.manchester.ManchesterRequest;
import ee.smta.api.manchester.ManchesterResponse;
import ee.smta.clients.HttpCall;
import ee.smta.clients.LondonClient;
import ee.smta.clients.ManchesterClient;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {LondonClient.class, ManchesterClient.class})
@Import({RestTemplate.class, OkHttpClient.class, HttpCall.class})
public class ClientCallTests {

    @Autowired
    LondonClient londonClient;
    @Autowired
    ManchesterClient manchesterClient;

    LondonResponse londonResponse;
    ManchesterResponse manchesterResponse;

    @Test
    void test_LondonRequestAvailableTime() throws JAXBException {
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
    void test_LondonRequestBooking() throws JAXBException {
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
    void test_londonBookingError() throws JAXBException {
        test_LondonRequestAvailableTime();

        String londonBookingTestUuid = londonResponse.getTireChangeTimesResponse().getAvailableTime().get(0).getUuid();
        String info = "TEST_INFO";
        dummyCallLondon(londonBookingTestUuid, info);
        assertAll(
                () -> assertThrows(BadRequestException.class, ()-> {
                    londonClient.bookTime(
                            LondonRequest.builder()
                                    .uuid(londonBookingTestUuid)
                                    .bookingInfo(info)
                                    .build());
                }),
                () -> assertThrows(BadRequestException.class, ()-> {
                    londonClient.bookTime(
                            LondonRequest.builder()
                                    .uuid(londonBookingTestUuid)
                                    .bookingInfo(info+1)
                                    .build());
                }));
    }

    @Test
    void test_ManchesterRequestAvailableTime() {
        ManchesterResponse manchesterResponse = manchesterClient.getAvailableTime(ManchesterRequest.builder()
                .from("2006-01-02")
                .build());

        assertAll(
                () -> assertFalse(manchesterResponse.getAvailableTimes().isEmpty(), "manchester availability list isEmpty"),
                () -> assertEquals(1500, manchesterResponse.getAvailableTimes().size(),"manchester availability list size")
        );

        this.manchesterResponse = manchesterResponse;
    }

    @Test
    void test_ManchesterRequestBooking() {
        test_ManchesterRequestAvailableTime();
        String manchesterBookingTestId = "";
        for(ManchesterResponse.AvailableTime availableTime: manchesterResponse.getAvailableTimes()) {
            if(availableTime.getAvailable().equals("true")) {
                manchesterBookingTestId = availableTime.getId();
                break;
            }
        }

        ManchesterResponse manchesterResponse = manchesterClient.bookTime(ManchesterRequest.builder()
                .id(manchesterBookingTestId)
                .contactInformation("TESTInfo")
                .build());

        String finalManchesterBookingTestId = manchesterBookingTestId;
        assertAll(
                () -> assertEquals(finalManchesterBookingTestId, manchesterResponse.getId(), "manchester booking id request response match"),
                () -> assertNotNull(manchesterResponse.getTime(), "manchester booked time isn't empty"),
                () -> assertEquals("false", manchesterResponse.getAvailable(), "manchester time is booked")
        );
    }

    @Test
    void test_ManchesterBookingError() {
        String dummyId = "20";
        dummyCallManchester(dummyId);
        assertAll(
                () -> assertThrows(BadRequestException.class, ()-> {
                    manchesterClient.bookTime(ManchesterRequest.builder()
                            .id(dummyId)
                            .contactInformation("TESTInfo")
                            .build());
                }),
                () -> assertThrows(BadRequestException.class, ()-> {
                    manchesterClient.bookTime(ManchesterRequest.builder()
                            .id(dummyId)
                            .contactInformation("")
                            .build());
                }));
    }

    private void dummyCallLondon(String id, String info) throws JAXBException {
        try {
            londonClient.bookTime(
                    LondonRequest.builder()
                            .uuid(id)
                            .bookingInfo(info)
                            .build());
        } catch (BadRequestException ignore) {};
    }

    private void dummyCallManchester(String id) {
        try {
            manchesterClient.bookTime(ManchesterRequest.builder()
                    .id(id)
                    .contactInformation("TESTInfo")
                    .build());
        } catch (BadRequestException ignore) {};
    }
}
