package ee.smta.clients;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class HttpCall {
    @Autowired
    OkHttpClient client;


    public String get(String requestUrl, String requestBody) {
        try {
            return client.newCall(new Request.Builder()
                            .url(requestUrl)
                            .get()
                            .build())
                    .execute().body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String put(String requestUrl, String requestBody) {
        try {
            return client.newCall(new Request.Builder()
                            .url(requestUrl)
                            .method("PUT", createRequestBodyBody(requestBody))
                            .build())
                    .execute().body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RequestBody createRequestBodyBody(String requestBody) {
        return okhttp3.RequestBody.create(requestBody,okhttp3.MediaType.parse(APPLICATION_JSON_VALUE));
    }


}
