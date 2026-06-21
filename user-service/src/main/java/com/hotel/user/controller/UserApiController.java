package com.hotel.user.controller;

import com.hotel.user.dto.CreateUserRequest;
import com.hotel.user.dto.UserDto;
import com.hotel.user.model.Rol;
import com.hotel.user.model.User;
import com.hotel.user.repository.RolRepository;
import com.hotel.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UserApiController(UserRepository userRepository, RolRepository rolRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserDto> findByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username exista deja"));
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email exista deja"));
        }
        String rolNume = req.getRol() != null ? req.getRol() : "USER";
        Rol rol = rolRepository.findByNume(rolNume)
                .orElseThrow(() -> new RuntimeException("Rol negasit: " + rolNume));

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roluri(Set.of(rol))
                .build();
        User saved = userRepository.save(user);
        log.info("User creat: {}", saved.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<?> schimbaRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            Rol rol = rolRepository.findByNume(body.get("rol"))
                    .orElseThrow(() -> new RuntimeException("Rol negasit"));
            user.setRoluri(Set.of(rol));
            userRepository.save(user);
            log.info("Rol schimbat pentru user {}: {}", user.getUsername(), rol.getNume());
            return ResponseEntity.ok(toDto(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        log.info("User sters: id={}", id);
        return ResponseEntity.noContent().build();
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .enabled(u.getEnabled())
                .roluri(u.getRoluri().stream().map(Rol::getNume).collect(Collectors.toSet()))
                .build();
    }
}
