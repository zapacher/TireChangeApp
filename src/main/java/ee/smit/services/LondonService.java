package ee.smit.services;

import ee.smit.clients.LondonClient;
import ee.smit.clients.api.london.LondonRequest;
import ee.smit.clients.api.london.LondonResponse;
import ee.smit.clients.api.london.TireChangeTimesResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.InternalServerErrorException;
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
    LondonClient londonClient;

    public <T> T process(Booking request, RequestType requestType) {
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
        LondonResponse londonResponse = londonClient.getAvailableTime(
                LondonRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .until(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        );

        System.out.println("================================");
        AvailableTimeResponse response = new AvailableTimeResponse();
        System.out.println("================================" + response.getVehicleTypes());
        response.getVehicleTypes().add(CAR);
        System.out.println("================================" + response);

        for(TireChangeTimesResponse.AvailableTime availableTime: londonResponse.getTireChangeTimesResponse().getAvailableTime()) {
            response.getAvailableTimeList().add(new AvailableTimeResponse.AvailableTime(String.valueOf(availableTime.getUuid()),availableTime.getTime()));
        }
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
}
