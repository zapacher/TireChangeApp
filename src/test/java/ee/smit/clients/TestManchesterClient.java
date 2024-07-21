package ee.smit.clients;

import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.HttpCall;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.configurations.ManchesterProperties;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@EnableConfigurationProperties(ManchesterProperties.class)
@SpringBootTest(classes = ManchesterClient.class)
@Import({RestTemplate.class, OkHttpClient.class, HttpCall.class})
public class TestManchesterClient {

    @Autowired
    ManchesterClient manchesterClient;
    @Mock
    HttpCall httpCall;

    ManchesterResponse manchesterResponse;

    @Test
    void test_ManchesterRequestAvailableTime() {
        ManchesterResponse manchesterResponse = manchesterClient.getAvailableTime(ManchesterRequest.builder()
                .from("2006-01-02")
                .build());

        assertAll(
                () -> assertFalse(manchesterResponse.getAvailableTimes().isEmpty(), "manchester response contains list")
        );

        this.manchesterResponse = manchesterResponse;
    }

    @Test
    void test_ManchesterRequestBooking() {
        test_ManchesterRequestAvailableTime();
        String manchesterBookingTestId = "";
        for(ManchesterResponse.AvailableTime availableTime: manchesterResponse.getAvailableTimes()) {
            if(availableTime.isAvailable()) {
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
                () -> assertFalse(manchesterResponse.isAvailable(), "manchester time is booked")
        );
    }

    @Test
    void test_ManchesterBookingError() {
        String dummyId = "20";
        dummyCallManchester(dummyId);
        assertAll(
                () -> assertThrows(BadRequestException.class,
                        () -> manchesterClient.bookTime(ManchesterRequest.builder()
                                .id(dummyId)
                                .contactInformation("TESTInfo")
                                .build())
                ),
                () -> assertThrows(BadRequestException.class,
                        () -> manchesterClient.bookTime(ManchesterRequest.builder()
                                .id(dummyId)
                                .contactInformation(null)
                                .build())
                ));
    }

    private void dummyCallManchester(String id) {
        try {
            manchesterClient.bookTime(ManchesterRequest.builder()
                    .id(id)
                    .contactInformation("TESTInfo")
                    .build());
        } catch (BadRequestException ignore) {
        }
    }
}