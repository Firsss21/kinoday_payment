package ru.kinoday.payment.payments.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Util {

    @Bean
    public Gson getGson() {
        return new GsonBuilder()
                .create();
    }

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
