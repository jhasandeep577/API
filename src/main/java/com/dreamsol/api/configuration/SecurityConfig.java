package com.dreamsol.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dreamsol.api.security.JwtAutenticationEntryPoint;
import com.dreamsol.api.security.JwtFilter;

@Configuration
@EnableWebMvc
public class SecurityConfig {
    @Autowired
    private JwtAutenticationEntryPoint entryPoint;
    @Autowired
    private JwtFilter filter;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDetailsService userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) 
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/auth/**","/swagger-ui/**", "/v3/api-docs/**")
                                .permitAll()
                                // .requestMatchers("/Department/**","/User-Type/**","/User-Permission/**").hasAuthority("Admin")
                                .requestMatchers(HttpMethod.DELETE).hasAuthority("ALL")
                                .requestMatchers(HttpMethod.POST).hasAuthority("ALL")
                                .requestMatchers(HttpMethod.PUT).hasAuthority("ALL")
                                .requestMatchers(HttpMethod.GET).hasAnyAuthority("READ","ALL")
                                .anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
