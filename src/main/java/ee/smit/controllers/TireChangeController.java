package ee.smit.controllers;

import ee.smit.api.Location;
import ee.smit.controllers.api.AvailableTimeResponse;
import ee.smit.controllers.api.BookingRequest;
import ee.smit.controllers.api.BookingResponse;
import ee.smit.services.LondonService;
import ee.smit.services.ManchesterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static ee.smit.api.RequestType.AVAILABLE_TIME;
import static ee.smit.api.RequestType.BOOKING;

@Slf4j
@RestController("tire_change")
@RequestMapping("/tire_change")
public class TireChangeController {
    @Autowired
    ManchesterService manchesterService;
    @Autowired
    LondonService londonService;

    @PostMapping("/getAvailableTime")
    List<AvailableTimeResponse> getAvailableTime(@RequestBody Location location) {
        List<AvailableTimeResponse> response = null;

        log.info("{} getAvailableTime Request: -> {}", this.getClass().getName(), location);

        switch(location) {
            case LONDON -> response = londonService.process(null, AVAILABLE_TIME);
            case MANCHESTER -> response = manchesterService.process(null, AVAILABLE_TIME);
        }

        log.info("{} getAvailableTime Response: -> {}", this.getClass().getName(), response);
        return response;
    }

    @PostMapping("/booking")
    BookingResponse booking(@RequestBody BookingRequest bookingRequest) {
        log.info("{} booking Request: -> {}", this.getClass().getName(), bookingRequest);

        BookingResponse response = null;

        switch(bookingRequest.getLocation()) {
            case LONDON -> response = londonService.process(bookingRequest, BOOKING);
            case MANCHESTER -> response = manchesterService.process(bookingRequest, BOOKING);
        }

        log.info("{} getAvailableTime Response: -> {}", this.getClass().getName(), response);
        return response;
    }
}
