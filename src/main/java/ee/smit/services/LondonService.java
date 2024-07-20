package ee.smit.services;

import ee.smit.clients.LondonClient;
import ee.smit.clients.api.london.LondonRequest;
import ee.smit.clients.api.london.LondonResponse;
import ee.smit.clients.api.london.TireChangeTimesResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.BadRequestException;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.configurations.LondonProperties;
import ee.smit.controllers.api.AvailableTimeResponse;
import ee.smit.controllers.api.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static ee.smit.commons.enums.VehicleTypes.CAR;

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
                return (T) getAvailableTime();
            }
            case BOOKING -> {
                return (T) booking((Booking) request);
            }
            default -> throw new InternalServerErrorException();
        }
    }

    private AvailableTimeResponse getAvailableTime() {
        LondonResponse londonResponse = londonClient.getAvailableTime(
                LondonRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .until(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        );

        AvailableTimeResponse response = new AvailableTimeResponse();
        response.getVehicleTypes().add(CAR);

        for(TireChangeTimesResponse.AvailableTime availableTime: londonResponse.getTireChangeTimesResponse().getAvailableTime()) {
            response.getAvailableTimeList().add(
                    new AvailableTimeResponse.AvailableTime(String.valueOf(availableTime.getUuid()),availableTime.getTime())
            );
        }
        response.setAddress(londonProperties.getAddress());
        return response;
    }

    private Booking booking(Booking request) {
        LondonResponse londonResponse = londonClient.bookTime(LondonRequest.builder()
                .uuid(UUID.fromString(request.getId()))
                .bookingInfo(request.getInfo())
                .build());

        return Booking.builder()
                .bookingTime(londonResponse.getTireChangeBookingResponse().getTime())
                .isBooked(true)
                .build();
    }

    private void isLocationAvailable() {
        if(!londonProperties.isAvailable()) {
            throw new BadRequestException(404, "service isn't available at the moment");
        }
    }
}
