package ee.smit.commons;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
@Slf4j
@Component
public class HttpCall {

    @Autowired
    OkHttpClient client;

    public Response post(String requestUrl, String requestBody) {
        return execute(new Request.Builder()
                .url(requestUrl)
                .post(createRequestBody(requestBody))
                .build());
    }

    public Response get(String requestUrl) {
        return execute(new Request.Builder()
                .url(requestUrl)
                .get()
                .build());
    }

    public Response put(String requestUrl, String requestBody) {
        return execute(new Request.Builder()
                .url(requestUrl)
                .put(createRequestBody(requestBody))
                .build());
    }

    private Response execute(Request request) {
        log.info("Before execution Request () -> {}", request);
        try {
            return client.newCall(request).execute();
        } catch (IOException ignore) {
            return null;
        }
    }

    private RequestBody createRequestBody(String requestBody) {
        return okhttp3.RequestBody.create(requestBody,okhttp3.MediaType.parse(APPLICATION_JSON_VALUE));
    }
}
