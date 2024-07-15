package ee.smit.services;

import ee.smit.api.RequestType;
import ee.smit.clients.ManchesterClient;
import ee.smit.clients.api.manchester.ManchesterRequest;
import ee.smit.clients.api.manchester.ManchesterResponse;
import ee.smit.commons.errors.InternalServerErrorException;
import ee.smit.controllers.dto.AvailableTimeResponse;
import ee.smit.controllers.dto.BookingRequest;
import ee.smit.controllers.dto.BookingResponse;
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

    public <T> T process(BookingRequest request, RequestType requestType) {
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

    private BookingResponse booking(BookingRequest bookingRequest) {
        ManchesterResponse manchesterResponse = manchesterClient.bookTime(
                ManchesterRequest.builder()
                        .id(bookingRequest.getId())
                        .contactInformation(bookingRequest.getInfo())
                        .build()
        );

        if(manchesterResponse.getErrorResponse() == null) {
            return BookingResponse.builder()
                    .id(manchesterResponse.getId())
                    .bookingTime(manchesterResponse.getTime())
                    .isBooked(manchesterResponse.isBooked())
                    .build();
        }

        return BookingResponse.builder()
                .errorResponse(manchesterResponse.getErrorResponse())
                .build();
    }
}
