package ee.smit.commons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonParser {
    static Gson gson = new Gson();

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    public static <T> List<T> fromJsonList(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, TypeToken.getParameterized(List.class, clazz).getType());
    }
}
