package ee.smta.clients;

import ee.smta.api.error.BadRequestException;
import ee.smta.api.error.InternalServerErrorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class HttpCall {

    @Autowired
    OkHttpClient client;

    protected String post(String requestUrl, String requestBody) {
        return execute(new Request.Builder()
                .url(requestUrl)
                .post(createRequestBody(requestBody))
                .build());
    }

    protected String get(String requestUrl) {
        return execute(new Request.Builder()
                .url(requestUrl)
                .get()
                .build());
    }

    protected String put(String requestUrl, String requestBody) {
        return execute(new Request.Builder()
                .url(requestUrl)
                .put(createRequestBody(requestBody))
                .build());
    }

    private String execute(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if(response.isSuccessful()) {
                if(Objects.requireNonNull(response.body()).string().isEmpty()) {
                    return response.body().string();
                }
                throw new InternalServerErrorException();
            } else {
                switch(response.code()) {
                    case 400 -> throw new BadRequestException(400, "Bad Request");
                    case 422 -> throw new BadRequestException(422, "This time is already booked");
                    default -> throw new InternalServerErrorException();
                }
            }
        } catch (IOException ignore) {
            throw new InternalServerErrorException();
        }
    }

    private RequestBody createRequestBody(String requestBody) {
        return okhttp3.RequestBody.create(requestBody,okhttp3.MediaType.parse(APPLICATION_JSON_VALUE));
    }
}
