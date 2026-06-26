package org.example.asq.config;

import lombok.RequiredArgsConstructor;
import org.example.asq.oauth.NaverAuthorizationRequestResolver;
import org.example.asq.oauth.NaverOAuth2UserService;
import org.example.asq.oauth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final NaverOAuth2UserService naverOAuth2UserService;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF: 쿠키에 토큰 저장(JS 읽기 가능), 폼은 Thymeleaf가 자동 삽입
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/user/login")
                .authorizationEndpoint(ep -> ep
                    .authorizationRequestResolver(
                        new NaverAuthorizationRequestResolver(clientRegistrationRepository)
                    )
                )
                .userInfoEndpoint(info -> info.userService(naverOAuth2UserService))
                .successHandler(oauth2LoginSuccessHandler)
            )
            .logout(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
