package com.hotel.hotel_management.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Resurse publice
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Doar ADMIN: structura hotel, angajati, inventar, tipuri camere, utilizatori
                .requestMatchers("/hoteluri/**").hasRole("ADMIN")
                .requestMatchers("/angajati/**").hasRole("ADMIN")
                .requestMatchers("/inventar-camere/**").hasRole("ADMIN")
                .requestMatchers("/tipuri-camere/**").hasRole("ADMIN")
                .requestMatchers("/utilizatori/**").hasRole("ADMIN")

                // ADMIN + RECEPTIONER: operatiuni zilnice
                .requestMatchers("/rezervari/**").hasAnyRole("ADMIN", "RECEPTIONER")
                .requestMatchers("/oaspeti/**").hasAnyRole("ADMIN", "RECEPTIONER")
                .requestMatchers("/facturi/**").hasAnyRole("ADMIN", "RECEPTIONER")
                .requestMatchers("/servicii/**").hasAnyRole("ADMIN", "RECEPTIONER")
                .requestMatchers("/rezervare-servicii/**").hasAnyRole("ADMIN", "RECEPTIONER")

                // Orice utilizator autentificat: home
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .rememberMe(rm -> rm
                .key("hotel-management-secret-key")
                .tokenValiditySeconds(1209600)
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
