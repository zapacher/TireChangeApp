package ee.smit.services;

import ee.smit.api.RequestType;
import ee.smit.clients.LondonClient;
import ee.smit.clients.api.london.LondonRequest;
import ee.smit.clients.api.london.LondonResponse;
import ee.smit.clients.api.london.TireChangeTimesResponse;
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
public class LondonService {

    @Autowired
    LondonClient londonClient;

    public <T> T process(BookingRequest request, RequestType requestType) {
        switch(requestType) {
            case AVAILABLE_TIME -> {
                return (T) getAvailableTime();
            }
            case BOOKING -> {
                return (T) booking((BookingRequest) request);
            }
            default -> throw new InternalServerErrorException();
        }
    }

    private List<AvailableTimeResponse> getAvailableTime() {
        TireChangeTimesResponse londonAvailableTime = londonClient.getAvailableTime(
                LondonRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .until(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        ).getTireChangeTimesResponse();

        List<AvailableTimeResponse> availableTimeDtoList = new ArrayList<>();
        for(TireChangeTimesResponse.AvailableTime availableTime: londonAvailableTime.getAvailableTime()) {
            availableTimeDtoList.add(new AvailableTimeResponse(availableTime.getUuid(), availableTime.getTime()));
        }

        return availableTimeDtoList;
    }

    private BookingResponse booking(BookingRequest bookingRequest) {
        LondonResponse londonResponse = londonClient.bookTime(LondonRequest.builder()
                .uuid(bookingRequest.getId())
                .bookingInfo(bookingRequest.getInfo())
                .build());

        if(londonResponse.getErrorResponse() == null) {
            return BookingResponse.builder()
                    .bookingTime(londonResponse.getTireChangeBookingResponse().getTime())
                    .isBooked(true)
                    .build();
        }

        return BookingResponse.builder()
                .errorResponse(londonResponse.getErrorResponse())
                .build();
    }
}
