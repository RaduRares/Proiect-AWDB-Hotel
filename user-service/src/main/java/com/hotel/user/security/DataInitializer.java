package com.hotel.user.security;

import com.hotel.user.model.Rol;
import com.hotel.user.model.User;
import com.hotel.user.repository.RolRepository;
import com.hotel.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) return;

        Rol adminRole = rolRepository.save(Rol.builder().nume("ADMIN").build());
        Rol receptionerRole = rolRepository.save(Rol.builder().nume("RECEPTIONER").build());
        Rol userRole = rolRepository.save(Rol.builder().nume("USER").build());

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@hotel.com")
                .password(passwordEncoder.encode("admin123"))
                .roluri(Set.of(adminRole))
                .build());

        userRepository.save(User.builder()
                .username("receptioner")
                .email("receptioner@hotel.com")
                .password(passwordEncoder.encode("recep123"))
                .roluri(Set.of(receptionerRole))
                .build());

        log.info("User-service: date initiale create.");
    }
}
