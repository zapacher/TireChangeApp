package ee.smta.clients;

import ee.smta.api.london.LondonRequest;
import ee.smta.api.london.LondonResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(
        path = "/api/crypto/ethereum/v1",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
public class LondonClient {
    @GetMapping
    public LondonResponse getAvailableBooking(@RequestBody LondonRequest londonRequest) {
        return LondonResponse.builder()
                .build();
    }

    @PutMapping
    public LondonResponse sendBooking(@RequestBody LondonRequest londonRequest) {
        return LondonResponse.builder()
                .build();
    }
}
