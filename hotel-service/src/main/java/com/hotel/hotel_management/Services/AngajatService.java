package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.Angajat;
import com.hotel.hotel_management.Models.Rol;
import com.hotel.hotel_management.Models.User;
import com.hotel.hotel_management.Repositories.AngajatRepository;
import com.hotel.hotel_management.Repositories.RolRepository;
import com.hotel.hotel_management.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AngajatService {

    private static final Logger log = LoggerFactory.getLogger(AngajatService.class);
    private final AngajatRepository angajatRepository;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AngajatService(AngajatRepository angajatRepository, UserRepository userRepository,
                          RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.angajatRepository = angajatRepository;
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Angajat> findAll(Pageable pageable) {
        log.debug("Finding all employees, page: {}", pageable);
        return angajatRepository.findAll(pageable);
    }

    public Page<Angajat> findByHotelId(Long hotelId, Pageable pageable) {
        log.debug("Finding employees for hotel: {}", hotelId);
        return angajatRepository.findByHotelId(hotelId, pageable);
    }

    public Angajat findById(Long id) {
        log.debug("Finding employee by id: {}", id);
        return angajatRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Angajat", id);
                });
    }

    public Angajat save(Angajat angajat) {
        log.info("Saving employee: {}", angajat.getNume_prenume());
        return angajatRepository.save(angajat);
    }

    public void deleteById(Long id) {
        log.info("Deleting employee with id: {}", id);
        angajatRepository.deleteById(id);
    }

    public Optional<String> autoCreateUser(Angajat angajat) {
        String functie = angajat.getFunctie();
        if (functie == null) return Optional.empty();

        String rolNume;
        String f = functie.toLowerCase();
        if (f.contains("manager") || f.contains("director")) {
            rolNume = "ADMIN";
        } else if (f.contains("receptioner") || f.contains("receptie")) {
            rolNume = "RECEPTIONER";
        } else {
            return Optional.empty();
        }

        String[] parts = angajat.getNume_prenume().trim().split("\\s+");
        String base = parts.length >= 2
                ? (parts[0].charAt(0) + "." + parts[parts.length - 1]).toLowerCase()
                : parts[0].toLowerCase();
        base = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-z0-9.]", "");

        String username = base;
        int suffix = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = base + suffix++;
        }

        String password = UUID.randomUUID().toString().substring(0, 8);

        Rol rol = rolRepository.findByNume(rolNume)
                .orElseThrow(() -> new ResourceNotFoundException("Rolul " + rolNume + " nu a fost gasit"));

        userRepository.save(User.builder()
                .username(username)
                .email(username + "@hotel.com")
                .password(passwordEncoder.encode(password))
                .hotel(angajat.getHotel())
                .roluri(Set.of(rol))
                .build());

        log.info("Auto-created user '{}' with role {} for angajat '{}'", username, rolNume, angajat.getNume_prenume());
        return Optional.of("Cont creat: username=" + username + " | parola=" + password + " | rol=" + rolNume);
    }
}
