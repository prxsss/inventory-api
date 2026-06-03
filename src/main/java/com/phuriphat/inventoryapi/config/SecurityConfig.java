package com.phuriphat.inventoryapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phuriphat.inventoryapi.common.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:4200"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))

            // Disable CSRF (not needed for stateless JWT)
            .csrf(AbstractHttpConfigurer::disable)

            // Configure endpoint authorization
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/auth/**",
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html"
                    ).permitAll()
                    .anyRequest().authenticated()
            )

            // Stateless session (required for JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    ApiErrorResponse.ErrorDetail errorDetail = ApiErrorResponse.ErrorDetail.builder()
                            .code("UNAUTHORIZED")
                            .message("Full authentication is required to access this resource")
                            .timestamp(LocalDateTime.now())
                            .path(request.getRequestURI())
                            .fieldErrors(null)
                            .build();

                    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                            .success(false)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .error(errorDetail)
                            .build();

                    response.setContentType("application/json");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    objectMapper.writeValue(response.getOutputStream(), errorResponse);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    ApiErrorResponse.ErrorDetail errorDetail = ApiErrorResponse.ErrorDetail.builder()
                            .code("ACCESS_DENIED")
                            .message("Access denied")
                            .timestamp(LocalDateTime.now())
                            .path(request.getRequestURI())
                            .fieldErrors(null)
                            .build();

                    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                            .success(false)
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .error(errorDetail)
                            .build();

                    response.setContentType("application/json");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    objectMapper.writeValue(response.getOutputStream(), errorResponse);
                })
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}