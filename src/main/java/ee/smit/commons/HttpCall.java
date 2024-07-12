package ee.smit.commons;

import ee.smit.commons.errors.InternalServerErrorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

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
        try {
            return client.newCall(request).execute();
        } catch (IOException ignore) {
            throw new InternalServerErrorException();
        }
    }

    private RequestBody createRequestBody(String requestBody) {
        return okhttp3.RequestBody.create(requestBody,okhttp3.MediaType.parse(APPLICATION_JSON_VALUE));
    }
}
