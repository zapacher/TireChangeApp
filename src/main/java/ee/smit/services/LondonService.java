package ee.smit.services;

import ee.smit.clients.LondonClient;
import ee.smit.clients.api.london.LondonDTO;
import ee.smit.clients.api.london.TireChangeTimesResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.LondonProperties;
import ee.smit.controller.api.AvailableTime;
import ee.smit.controller.api.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class LondonService {

    @Autowired
    LondonProperties londonProperties;
    @Autowired
    LondonClient londonClient;

    public <T> T process(T request, RequestType requestType) {
        isLocationAvailable();

        switch(requestType) {
            case AVAILABLE_TIME -> {
                return (T) getAvailableTime((AvailableTime) request);
            }
            case BOOKING -> {
                return (T) booking((Booking) request);
            }
            default -> throw new InternalServerErrorException();
        }
    }

    private AvailableTime getAvailableTime(AvailableTime request) {
        final LocalDate userCurrentDate = Instant.parse(request.getUserTime()).atZone(ZoneId.of("UTC")).toLocalDate();

        LondonDTO londonResponse = londonClient.getAvailableTime(
                LondonDTO.builder()
                        .tireChangeBookingRequest(LondonDTO.TireChangeBookingRequest.builder()
                                .from(userCurrentDate)
                                .until(userCurrentDate.plusMonths(londonProperties.getConfig().getMonthsRange()))
                                .build())
                        .build()
        );

        AvailableTime response = new AvailableTime();
        response.getVehicleTypes().addAll(londonProperties.getVehicleTypes());

        for(TireChangeTimesResponse.AvailableTime availableTime: londonResponse.getTireChangeTimesResponse().getAvailableTime()) {
            if(ZonedDateTime.parse(availableTime.getTime()).isAfter(ZonedDateTime.parse(request.getUserTime()))) {
                response.getAvailableTimeList().add(
                        new AvailableTime.AvailableTimeList(String.valueOf(availableTime.getUuid()), availableTime.getTime())
                );
            }
        }

        response.setAddress(londonProperties.getAddress());
        response.setLocation(londonProperties.getLocation());

        return response;
    }

    private Booking booking(Booking request) {
        /*
         !!WARNING!! if info for uuid is equal as the booked one, it will be successfully booked. For that,
          repeat Request of available booking time before execution call.
         */
        checkAvailability(request);

        LondonDTO response = londonClient.bookTime(
                LondonDTO.builder()
                        .tireChangeBookingRequest(LondonDTO.TireChangeBookingRequest.builder()
                                .uuid(UUID.fromString(request.getId()))
                                .bookingInfo(request.getInfo())
                                .build())
                        .build()
        );

        return Booking.builder()
                .bookingTime(response.getTireChangeBookingResponse().getTime())
                .isBooked(true)
                .build();
    }

    private void checkAvailability(Booking request) {
        AvailableTime requestForCheck = new AvailableTime();
        requestForCheck.setUserTime(request.getUserTime());
        AvailableTime response = getAvailableTime(requestForCheck);

        if(response.getAvailableTimeList()
                .stream()
                .noneMatch(availableTime -> availableTime.getId().equals(request.getId()))) {
            throw new BadRequestException(422, "This time is already booked");
        }
    }

    private void isLocationAvailable() {
        if(!londonProperties.isAvailable()) {
            throw new BadRequestException(404, "service isn't available at the moment");
        }
    }
}
