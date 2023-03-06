package com.rstejskalprojects.reservationsystem.security.config;

import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

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
                .antMatchers("/api/reservations/{userId}", "/api/reservations/{userId}/present",
                        "/api/reservations/create", "/api/reservations", "/api/reservations/delete/**").authenticated()
                .antMatchers("/api/events/create", "/api/events/delete-recurrent/**", "/api/events/delete/**",
                        "/api/events/update-recurrent/**", "/api/events/update/**", "/api/locations/create",
                        "/api/locations/delete/**", "/api/reservations", "/api/users/**").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/api/access/**", "/api/auth/**", "/api/events", "/swagger-ui.html/**", "/api/locations", "/api/token/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // TODO authorize
                .anyRequest().authenticated()
                .and()

//                .addFilter(customAuthenticationFilter);
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
