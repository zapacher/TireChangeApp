package ee.smit.services;

import ee.smit.clients.ManchesterClient;
import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.ManchesterProperties;
import ee.smit.controllers.api.AvailableTime;
import ee.smit.controllers.api.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ManchesterService {

    @Autowired
    ManchesterProperties manchesterProperties;
    @Autowired
    ManchesterClient manchesterClient;

    public <T> T process(Booking request, RequestType requestType) {
        isLocationAvailable();

        switch(requestType) {
            case AVAILABLE_TIME -> {
                return (T) getAvailableTime();
            }
            case BOOKING -> {
                return (T) booking(request);
            }
            default -> throw new InternalServerErrorException();
        }
    }

    private AvailableTime getAvailableTime() {
        ManchesterResponse manchesterResponse = manchesterClient.getAvailableTime(
                ManchesterRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        );

        AvailableTime response = new AvailableTime();
        response.setVehicleTypes(manchesterProperties.getVehicleTypes());

        for(ManchesterResponse.AvailableTime availableTime: manchesterResponse.getAvailableTimes()) {
            response.getAvailableTimeList().add(new AvailableTime.AvailableTimeList(availableTime.getId(), availableTime.getTime()));
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
