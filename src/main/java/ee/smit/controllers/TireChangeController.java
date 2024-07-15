package ee.smit.controllers;

import ee.smit.api.Location;
import ee.smit.controllers.api.AvailableTimeResponse;
import ee.smit.controllers.api.BookingRequest;
import ee.smit.controllers.api.BookingResponse;
import ee.smit.services.LondonService;
import ee.smit.services.ManchesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static ee.smit.api.RequestType.AVAILABLE_TIME;
import static ee.smit.api.RequestType.BOOKING;

@RestController("tire_change")
@RequestMapping("/tire_change")
public class TireChangeController {
    @Autowired
    ManchesterService manchesterService;
    @Autowired
    LondonService londonService;

    @PostMapping("/getAvailableTime")
    List<AvailableTimeResponse> getAvailableTime(@RequestBody Location location) {
        List<AvailableTimeResponse> availableTimeDtoList = null;

        switch(location) {
            case LONDON -> availableTimeDtoList = londonService.process(null, AVAILABLE_TIME);
            case MANCHESTER -> availableTimeDtoList = manchesterService.process(null, AVAILABLE_TIME);
        }
        return availableTimeDtoList;
    }

    @PostMapping("/booking")
    BookingResponse booking(@RequestBody BookingRequest bookingRequest) {
        BookingResponse bookingResult = null;

        switch(bookingRequest.getLocation()) {
            case LONDON -> bookingResult = londonService.process(bookingRequest, BOOKING);
            case MANCHESTER -> bookingResult = manchesterService.process(bookingRequest, BOOKING);
        }
        return bookingResult;
    }
}
