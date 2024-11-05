package ee.smit.controller;


import ee.smit.clients.LondonClient;
import ee.smit.clients.ManchesterClient;
import ee.smit.commons.HttpCall;
import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import ee.smit.configurations.LocationProperties;
import ee.smit.configurations.LondonProperties;
import ee.smit.configurations.ManchesterProperties;
import ee.smit.controller.api.AvailableLocationsResponse;
import ee.smit.controller.api.AvailableTime;
import ee.smit.services.LondonService;
import ee.smit.services.ManchesterService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@EnableConfigurationProperties({ManchesterProperties.class, LondonProperties.class})
@SpringBootTest(classes = {TireChangeController.class})
@Import({RestTemplate.class, OkHttpClient.class, HttpCall.class, ManchesterService.class, LondonService.class,
        ManchesterClient.class, LondonClient.class, LocationProperties.class})
@Disabled
public class TestTireChangeController {

    @Autowired
    TireChangeController controller;

    AvailableLocationsResponse locationsResponse;

    @Test
    void test_getAvaialableLoations() {
        getLocations();
        assertNotNull(locationsResponse);
    }

    @Test
    void test_getAvaialbleTimeLocations() {
        getLocations();

        for (Map.Entry<Locations, List<VehicleTypes>> entry : locationsResponse.getAvailableLocations().entrySet()) {
            assertNotNull(controller.availableTime(buildAvailableTimeRequest(ZonedDateTime.now(ZoneOffset.UTC).toString(), entry.getKey())),
                    entry.getKey().name() + " response was null");
        }
    }

    void getLocations() {
        locationsResponse = controller.availableLocations();
    }

    AvailableTime buildAvailableTimeRequest(String userTime, Locations location) {
        AvailableTime availableTime = new AvailableTime();
        availableTime.setUserTime(userTime);
        availableTime.setLocation(location);
        return availableTime;
    }
}
