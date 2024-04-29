package com.example.sportsreservationsystembackend.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class is used to configure web security
 * @author Radim Stejskal
 */

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    /**
     * This method is used to configure security filter chain
     * @param http HttpSecurity object
     * @return SecurityFilterChain object
     * @throws Exception
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(mgmt -> mgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/calendars").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH, "/api/v1/calendars").hasRole(ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/v1/calendars/{calendarId}").hasRole(ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/v1/calendars/{calendarId}/event").hasRole(ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/v1/calendars/{calendarId}/event/reservations").hasAnyRole(USER, ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/password").hasAnyRole(USER, ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/role").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/balance").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/discount").hasAnyRole(USER, ADMIN)
                .requestMatchers("/api/v1/reservations/**").hasAnyRole(USER, ADMIN)
                .requestMatchers("/api/v1/events/**").hasRole(ADMIN)
                .anyRequest().permitAll())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * This method is used to configure authentication manager
     * @param authenticationConfiguration
     * @return AuthenticationManager object
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * This method is used to configure DaoAuthenticationProvider
     * @return DaoAuthenticationProvider object
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}

