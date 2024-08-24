package ee.smit.controller;

import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.configurations.LocationProperties;
import ee.smit.controller.api.AvailableLocationsResponse;
import ee.smit.controller.api.AvailableTime;
import ee.smit.controller.api.Booking;
import ee.smit.controller.api.groups.Request;
import ee.smit.services.LondonService;
import ee.smit.services.ManchesterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    LocationProperties locationProperties;

    @GetMapping("/availableLocations")
    public AvailableLocationsResponse availableLocations() {
        log.info("getLocations Request");

        AvailableLocationsResponse response = new AvailableLocationsResponse(locationProperties.getAvailableLocations());

        log.info("getLocations Response: -> {}", response);
        return response;
    }

    @PostMapping("/availableTime")
    public AvailableTime availableTime(@Validated (Request.class) @RequestBody AvailableTime request) {
        log.info("getAvailableTime Request: -> {}", request);

        AvailableTime response = controller(request, request.getLocation(), AVAILABLE_TIME);

        log.info("getAvailableTime Response: -> {}", response);
        return response;
    }

    @PostMapping("/booking")
    public Booking booking(@Validated (Request.class) @RequestBody Booking request) {
        log.info("booking Request: -> {}", request);

        Booking response = controller(request, request.getLocation(), BOOKING);

        log.info("booking Response: -> {}", response);
        return response;
    }

    private <T> T controller(T request, Locations location, RequestType requestType) {
        switch(location) {
            case LONDON -> {
                return londonService.process(request, requestType);
            }
            case MANCHESTER -> {
                return manchesterService.process(request, requestType);
            }
            default -> {
                throw new BadRequestException(500, "Something went wrong");
            }
        }
    }
}
