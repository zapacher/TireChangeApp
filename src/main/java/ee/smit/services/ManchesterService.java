package ee.smit.services;

import ee.smit.clients.ManchesterClient;
import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.ManchesterProperties;
import ee.smit.controller.api.AvailableTime;
import ee.smit.controller.api.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class ManchesterService {

    @Autowired
    ManchesterProperties manchesterProperties;
    @Autowired
    ManchesterClient manchesterClient;

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

        ManchesterResponse manchesterResponse = manchesterClient.getAvailableTime(
                ManchesterRequest.builder()
                        .from(userCurrentDate)
                        .build()
        );

        AvailableTime response = new AvailableTime();
        response.setVehicleTypes(manchesterProperties.getVehicleTypes());

        for(ManchesterResponse.AvailableTime availableTime: manchesterResponse.getAvailableTimes()) {
            if(ZonedDateTime.parse(availableTime.getTime()).isAfter(ZonedDateTime.parse(request.getUserTime()))) {
                response.getAvailableTimeList().add(
                        new AvailableTime.AvailableTimeList(String.valueOf(availableTime.getId()), availableTime.getTime())
                );
            }
        }

        response.setAddress(manchesterProperties.getAddress());
        response.setLocation(manchesterProperties.getLocation());
        return response;
    }

    private Booking booking(Booking request) {
        ManchesterResponse manchesterResponse = manchesterClient.bookTime(
                ManchesterRequest.builder()
                        .id(request.getId())
                        .contactInformation(request.getInfo())
                        .build()
        );

        return Booking.builder()
                .id(manchesterResponse.getId())
                .bookingTime(manchesterResponse.getTime())
                .isBooked(!manchesterResponse.isAvailable())
                .build();
    }

    private void isLocationAvailable() {
        if(!manchesterProperties.isAvailable()) {
            throw new BadRequestException(404, "service isn't available at the moment");
        }
    }
}
