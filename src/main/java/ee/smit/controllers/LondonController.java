package ee.smit.controllers;

import ee.smit.api.london.LondonRequest;
import ee.smit.api.london.LondonResponse;
import ee.smit.api.london.TireChangeTimesResponse;
import ee.smit.clients.LondonClient;
import ee.smit.controllers.dto.AvailableTimeDto;
import ee.smit.controllers.dto.BookingDto;
import ee.smit.controllers.dto.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalTime.now;
import static java.time.temporal.ChronoUnit.YEARS;

@RestController("london")
@RequestMapping("/london")
public class LondonController {

    @Autowired
    LondonClient londonClient;

    @GetMapping("getAvailableTime")
    List<AvailableTimeDto> getAvailableTime() {
        TireChangeTimesResponse londonAvailableTime = londonClient.getAvailableTime(
                LondonRequest.builder()
                        .from(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .until(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        ).getTireChangeTimesResponse();
        System.out.println(londonAvailableTime.getAvailableTime().size());

        List<AvailableTimeDto> availableTimeDtoList = new ArrayList<>();
        for(TireChangeTimesResponse.AvailableTime availableTime: londonAvailableTime.getAvailableTime()) {
            availableTimeDtoList.add(new AvailableTimeDto(availableTime.getUuid(), availableTime.getTime()));
        }

        return availableTimeDtoList;
    }

    @PostMapping("/booking")
    BookingDto booking(@Validated (Request.class) @RequestBody BookingDto bookingDto) {
        LondonResponse londonResponse = londonClient.bookTime(LondonRequest.builder()
                .uuid(bookingDto.getId())
                .bookingInfo(bookingDto.getInfo())
                .build());

        if(londonResponse.getError() == null) {
            return BookingDto.builder()
                    .bookingTime(londonResponse.getTireChangeBookingResponse().getTime())
                    .isBooked(true)
                    .build();
        }

        return BookingDto.builder()
                .errorResponse(londonResponse.getError())
                .build();
    }
}
