package ru.gorvat.book.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
class OAuthClientHttpRequestInterceptorTest {

    @Mock
    OAuth2AuthorizedClientManager authorizedClientManager;

    OAuthClientHttpRequestInterceptor interceptor;

    @BeforeEach
    void setUp () {
        this.interceptor = new OAuthClientHttpRequestInterceptor (this.authorizedClientManager,
                "test");
    }

    @Test
    void intercept_AuthorizationHeaderIsNotSet_AddsAuthorizationHeader () throws Exception {

        var request = new MockClientHttpRequest ();
        var body = new byte[0];
        var execution = mock (ClientHttpRequestExecution.class);
        var response = new MockClientHttpResponse ();
        var authentication = new TestingAuthenticationToken ("s.gorvat", "gorvat");
        SecurityContextHolder.getContext ().setAuthentication (authentication);
        var authorizedClient = new OAuth2AuthorizedClient (mock (), "s.gorvat",
                new OAuth2AccessToken (OAuth2AccessToken.TokenType.BEARER, "token",
                        Instant.now (), Instant.MAX));
        doReturn (authorizedClient).when (this.authorizedClientManager)
                .authorize (argThat (authorizationRequest ->
                        authorizationRequest.getPrincipal ().equals (authentication) &&
                        authorizationRequest.getClientRegistrationId ().equals ("test")));
        doReturn (response).when (execution).execute(request, body);

        var result = this.interceptor.intercept(request, body, execution);

        assertEquals(response, result);
        assertEquals ("Bearer token", request.getHeaders ()
                .getFirst (HttpHeaders.AUTHORIZATION));
        verify (execution).execute (request, body);
        verifyNoMoreInteractions (execution);
    }
}
