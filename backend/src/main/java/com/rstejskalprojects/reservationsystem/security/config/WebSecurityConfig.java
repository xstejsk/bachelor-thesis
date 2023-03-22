package com.rstejskalprojects.reservationsystem.security.config;

import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtFilter jwtFilter;
    private AuthenticationEntryPoint authenticationEntryPoint;
//    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Override
    public void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
        super.setAuthenticationConfiguration(authenticationConfiguration);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("api/v1/token", "api/v1/token/**", "api/v1/confirmations/**",
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/role").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/ban-status").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users/password-reset").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/users/{userId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/events/{eventId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v1/events/{eventId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/events/recurrent/{groupId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v1/events/recurrent/{groupId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/events").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/confirmations/submit-token").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/confirmations/resend-confirmation").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/token").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/token/refresh").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/reservations").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v1/reservations").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/reservations/{userId}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/reservations/{reservationId}").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/locations").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/locations").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v1/locations/{locationId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
