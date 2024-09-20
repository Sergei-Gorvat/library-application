package ru.gorvat.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Configuration
public class SecurityBeans {

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests (authorizeHttpRequest ->
                        authorizeHttpRequest.anyRequest ( ).hasRole ("MANAGER"))
                .oauth2Login (Customizer.withDefaults ( ))
                .oauth2Client (Customizer.withDefaults ( ))
                .build ( );
    }

    @Bean
    OAuth2UserService <OidcUserRequest, OidcUser> oAuth2UserService ( ) {
        OidcUserService oidcUserService = new OidcUserService ( );
        return userRequest -> {
            OidcUser oidcUser = oidcUserService.loadUser (userRequest);
            List <GrantedAuthority> authorities =
                    Optional.ofNullable (oidcUser.getClaimAsStringList ("groups"))
                            .orElseGet (List :: of)
                            .stream ( )
                            .filter (role -> role.startsWith ("ROLE_"))
                            .map (SimpleGrantedAuthority :: new)
                            .map (GrantedAuthority.class :: cast)
                            .toList ( );

            return new DefaultOidcUser (authorities, oidcUser.getIdToken ( ), oidcUser.getUserInfo ( ));
        };
    }
}
