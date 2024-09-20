package ru.gorvat.book.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.gorvat.book.client.BookRestClientImpl;
import ru.gorvat.book.security.OAuthClientHttpRequestInterceptor;

@Configuration
public class ClientBeans {

    @Bean
    public BookRestClientImpl bookRestClient (
            @Value ("${gorvat/services/book/uri:http://localhost:8081}") String bookBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository clientRepository,
            @Value ("${gorvat/services/book/registration:keycloak}") String registrationId) {
        return new BookRestClientImpl (RestClient.builder ()
                .baseUrl (bookBaseUri)
                .requestInterceptor (new OAuthClientHttpRequestInterceptor (
                        new DefaultOAuth2AuthorizedClientManager (
                                clientRegistrationRepository, clientRepository), registrationId))
                .build ());
    }
}
