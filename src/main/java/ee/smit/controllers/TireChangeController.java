package ee.smit.controllers;

import ee.smit.controllers.api.AvailableTimeRequest;
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

import static ee.smit.api.Location.LONDON;
import static ee.smit.api.Location.MANCHESTER;
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
    List<AvailableTimeResponse> getAvailableTime(@RequestBody AvailableTimeRequest request) {
        List<AvailableTimeResponse> response = null;

        log.info("{} getAvailableTime Request: -> {}", this.getClass().getName(), request);

        switch(request.getLocation()) {
            case LONDON -> response = londonService.process(null, AVAILABLE_TIME);
            case MANCHESTER -> response = manchesterService.process(null, AVAILABLE_TIME);
        }

        log.info("{} getAvailableTime Response: -> {}", this.getClass().getName(), response);
        return response;
    }

    @PostMapping("/booking")
    BookingResponse booking(@RequestBody BookingRequest request) {
        log.info("{} booking Request: -> {}", this.getClass().getName(), request);

        BookingResponse response = null;

        switch(request.getLocation()) {
            case LONDON -> response = londonService.process(request, BOOKING);
            case MANCHESTER -> response = manchesterService.process(request, BOOKING);
        }

        log.info("{} booking Response: -> {}", this.getClass().getName(), response);
        return response;
    }
}
