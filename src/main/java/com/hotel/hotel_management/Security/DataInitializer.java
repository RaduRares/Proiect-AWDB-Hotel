package com.hotel.hotel_management.Security;

import com.hotel.hotel_management.Models.Rol;
import com.hotel.hotel_management.Models.User;
import com.hotel.hotel_management.Repositories.RolRepository;
import com.hotel.hotel_management.Repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {
    
    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(RolRepository rolRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostConstruct
    public void init() {
        // Create roles if they don't exist
        if (rolRepository.count() == 0) {
            Rol adminRole = Rol.builder().nume("ADMIN").build();
            Rol recepRole = Rol.builder().nume("RECEPTIONER").build();
            Rol userRole = Rol.builder().nume("USER").build();
            
            rolRepository.saveAll(List.of(adminRole, recepRole, userRole));
            
            // Create admin user
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@hotel.com")
                    .roluri(Set.of(adminRole))
                    .build();
            
            userRepository.save(admin);
            
            System.out.println("✓ Initial roles and admin user created successfully!");
        }
    }
}
