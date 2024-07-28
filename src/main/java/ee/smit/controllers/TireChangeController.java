package ee.smit.controllers;

import ee.smit.commons.enums.Locations;
import ee.smit.commons.enums.VehicleTypes;
import ee.smit.configurations.LocationProperties;
import ee.smit.controllers.api.AvailableTime;
import ee.smit.controllers.api.Booking;
import ee.smit.controllers.api.groups.Request;
import ee.smit.services.LondonService;
import ee.smit.services.ManchesterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    LocationProperties locationProperties;

    @GetMapping("/availableLocations")
    public HashMap<Locations, List<VehicleTypes>> getLocations() {
        log.info("getLocations Request");

        HashMap<Locations, List<VehicleTypes>> response = locationProperties.getAvailableLocations();

        log.info("getLocations Response: -> {}", response);
        return response;
    }

    @PostMapping("/getAvailableTime")
    public AvailableTime getAvailableTime(@Validated (Request.class) @RequestBody AvailableTime request) {
        AvailableTime response = null;
        log.info("getAvailableTime Request: -> {}", request);

        switch(request.getLocation()) {
            case LONDON -> response = londonService.process(request, AVAILABLE_TIME);
            case MANCHESTER -> response = manchesterService.process(null, AVAILABLE_TIME);
        }

        log.info("getAvailableTime Response: -> {}", response);
        return response;
    }

    @PostMapping("/booking")
    public Booking booking(@Validated (Request.class) @RequestBody Booking request) {
        log.info("booking Request: -> {}", request);

        Booking response = null;

        switch(request.getLocation()) {
            case LONDON -> response = londonService.process(request, BOOKING);
            case MANCHESTER -> response = manchesterService.process(request, BOOKING);
        }

        log.info("booking Response: -> {}", response);
        return response;
    }
}
