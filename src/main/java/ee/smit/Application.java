package ee.smit;


import ee.smit.configurations.LondonProperties;
import ee.smit.configurations.ManchesterProperties;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties({LondonProperties.class, ManchesterProperties.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}