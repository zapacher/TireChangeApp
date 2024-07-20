package ee.smit.controllers;

import ee.smit.commons.enums.Locations;
import ee.smit.configurations.Properties;
import ee.smit.controllers.api.AvailableTimeResponse;
import ee.smit.controllers.api.Booking;
import ee.smit.services.LondonService;
import ee.smit.services.ManchesterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ee.smit.commons.enums.RequestType.AVAILABLE_TIME;
import static ee.smit.commons.enums.RequestType.BOOKING;

@Slf4j
@RestController("tire_change")
@RequestMapping(
        path = "/tire_change",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
public class TireChangeController {

    @Autowired
    ManchesterService manchesterService;
    @Autowired
    LondonService londonService;
    @Autowired
    Properties properties;

    @GetMapping("/availableServices")
    List<Locations> getServices() {
        return properties.getAvailableServices();
    }

    @PostMapping("/getAvailableTime")
    AvailableTimeResponse getAvailableTime(@RequestBody Locations request) {
        AvailableTimeResponse response = null;
        log.info("{} getAvailableTime Request: -> {}", this.getClass().getName(), request);

        switch(request) {
            case LONDON -> response = londonService.process(null, AVAILABLE_TIME);
            case MANCHESTER -> response = manchesterService.process(null, AVAILABLE_TIME);
        }
        response.setLocation(request);

        log.info("{} getAvailableTime Response: -> {}", this.getClass().getName(), response);
        return response;
    }

    @PostMapping("/booking")
    Booking booking(@RequestBody Booking request) {
        log.info("{} booking Request: -> {}", this.getClass().getName(), request);

        Booking response = null;

        switch(request.getLocation()) {
            case LONDON -> response = londonService.process(request, BOOKING);
            case MANCHESTER -> response = manchesterService.process(request, BOOKING);
        }

        log.info("{} booking Response: -> {}", this.getClass().getName(), response);

        return response;
    }

}
