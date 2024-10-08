package ru.gorvat.book.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.gorvat.book.client.BookRestClientImpl;

import static org.mockito.Mockito.mock;

@Configuration
public class TestingBeans {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository () {
        return mock (ClientRegistrationRepository.class);
    }

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository () {
        return mock (OAuth2AuthorizedClientRepository.class);
    }

    @Bean
    @Primary
    public BookRestClientImpl testBookRestClientImpl (
            @Value ("${gorvat.services.book.uri:http://localhost:54321}") String bookBaseUri
    ) {
        return new BookRestClientImpl (RestClient.builder()
                .baseUrl (bookBaseUri)
                .requestFactory (new JdkClientHttpRequestFactory ())
                .build());
    }
}
