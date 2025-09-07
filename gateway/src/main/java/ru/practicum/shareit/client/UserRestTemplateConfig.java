package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.client.UserClient;

@Configuration
public class UserRestTemplateConfig {
    @Autowired
    private RestTemplateBuilder builder;

    @Bean
    @Qualifier("userRestTemplate")
    public RestTemplate userRestTemplate(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + UserClient.API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

}
