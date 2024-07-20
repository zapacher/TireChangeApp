package ee.smit.services;

import ee.smit.clients.ManchesterClient;
import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.ManchesterProperties;
import ee.smit.controllers.api.AvailableTimeResponse;
import ee.smit.controllers.api.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ee.smit.commons.enums.VehicleTypes.CAR;
import static ee.smit.commons.enums.VehicleTypes.TRUCK;

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

    private AvailableTimeResponse getAvailableTime() {
        ManchesterResponse manchesterResponse = manchesterClient.getAvailableTime(
                ManchesterRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        );

        AvailableTimeResponse response = new AvailableTimeResponse();
        response.setVehicleTypes(List.of(CAR, TRUCK));

        for(ManchesterResponse.AvailableTime availableTime: manchesterResponse.getAvailableTimes()) {
            response.getAvailableTimeList().add(new AvailableTimeResponse.AvailableTime(availableTime.getId(), availableTime.getTime()));
        }
        response.setAddress(manchesterProperties.getAddress());
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
