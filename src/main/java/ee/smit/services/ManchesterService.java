package ee.smit.services;

import ee.smit.clients.ManchesterClient;
import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.enums.RequestType;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.controllers.api.AvailableTimeResponse;
import ee.smit.controllers.api.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManchesterService {

    @Autowired
    ManchesterClient manchesterClient;

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

    private List<AvailableTimeResponse> getAvailableTime() {
        ManchesterResponse manchesterResponse = manchesterClient.getAvailableTime(
                ManchesterRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        );

        List<AvailableTimeResponse> availableTimeResponse = new ArrayList<>();
        for(ManchesterResponse.AvailableTime availableTime: manchesterResponse.getAvailableTimes()) {
            availableTimeResponse.add(new AvailableTimeResponse(availableTime.getId(), availableTime.getTime()));
        }

        return availableTimeResponse;
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
}
