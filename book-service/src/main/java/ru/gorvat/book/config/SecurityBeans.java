package ru.gorvat.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityBeans {

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests (authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers (HttpMethod.POST, "/book-api/books")
                        .hasAuthority ("SCOPE_edit_catalogue")
                        .requestMatchers (HttpMethod.PATCH, "/book-api/books/{bookId:\\d}")
                        .hasAuthority ("SCOPE_edit_catalogue")
                        .requestMatchers (HttpMethod.DELETE, "/book-api/books/{bookId:\\d}")
                        .hasAuthority ("SCOPE_edit_catalogue")
                        .requestMatchers (HttpMethod.GET)
                        .hasAuthority ("SCOPE_view_catalogue")
                        .anyRequest ( ).denyAll ( ))
                .csrf (CsrfConfigurer :: disable)
                .sessionManagement (sessionManager -> sessionManager
                        .sessionCreationPolicy (SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer (oauth2ResourceServer -> oauth2ResourceServer
                        .jwt (Customizer.withDefaults ( )))
                .build ( );
    }
}
