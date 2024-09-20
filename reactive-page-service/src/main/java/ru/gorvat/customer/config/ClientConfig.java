package ru.gorvat.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.gorvat.customer.client.WebClientBooksClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientBooksClient webClientBooksClient (
            @Value ("${gorvat/services/book/uri:http://localhost:8081}") String bookBaseUri) {
        return new WebClientBooksClient (WebClient.builder ()
                .baseUrl (bookBaseUri)
                .build ());
    }
}
