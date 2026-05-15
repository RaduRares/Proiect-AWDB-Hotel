package com.hotel.hotel_management.Security;

import com.hotel.hotel_management.Models.*;
import com.hotel.hotel_management.Repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final TipCameraRepository tipCameraRepository;
    private final OaspeteRepository oaspeteRepository;
    private final AngajatRepository angajatRepository;
    private final ServiciuRepository serviciuRepository;

    public DataInitializer(RolRepository rolRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, HotelRepository hotelRepository,
                           TipCameraRepository tipCameraRepository, OaspeteRepository oaspeteRepository,
                           AngajatRepository angajatRepository, ServiciuRepository serviciuRepository) {
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hotelRepository = hotelRepository;
        this.tipCameraRepository = tipCameraRepository;
        this.oaspeteRepository = oaspeteRepository;
        this.angajatRepository = angajatRepository;
        this.serviciuRepository = serviciuRepository;
    }

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            log.info("Demo data already present, skipping initialization.");
            return;
        }
        log.info("Initializing demo data...");

        // Roles
        Rol adminRole = rolRepository.save(Rol.builder().nume("ADMIN").build());
        Rol receptionerRole = rolRepository.save(Rol.builder().nume("RECEPTIONER").build());
        Rol userRole = rolRepository.save(Rol.builder().nume("USER").build());

        // Users
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

        userRepository.save(User.builder()
                .username("oaspete")
                .email("oaspete@hotel.com")
                .password(passwordEncoder.encode("oaspete123"))
                .roluri(Set.of(userRole))
                .build());

        // Hotels
        Hotel h1 = hotelRepository.save(Hotel.builder()
                .nume("Hotel Grand Palace")
                .stele(5)
                .adresa("Str. Victoriei 10")
                .oras("Bucuresti")
                .telefon_rezervari("0211234567")
                .email_rezervari("rezervari@grandpalace.ro")
                .build());

        Hotel h2 = hotelRepository.save(Hotel.builder()
                .nume("Hotel Carpati")
                .stele(3)
                .adresa("Bd. Eroilor 25")
                .oras("Brasov")
                .telefon_rezervari("0268123456")
                .email_rezervari("contact@carpati.ro")
                .build());

        // Room types
        tipCameraRepository.save(TipCamera.builder()
                .nume("Single Standard")
                .descriere("Camera single cu pat simplu")
                .pret(new BigDecimal("200.00"))
                .capacitate(1)
                .hotel(h1)
                .build());

        tipCameraRepository.save(TipCamera.builder()
                .nume("Double Deluxe")
                .descriere("Camera dubla cu vedere la oras")
                .pret(new BigDecimal("450.00"))
                .capacitate(2)
                .hotel(h1)
                .build());

        tipCameraRepository.save(TipCamera.builder()
                .nume("Single Economy")
                .descriere("Camera single economica")
                .pret(new BigDecimal("120.00"))
                .capacitate(1)
                .hotel(h2)
                .build());

        // Guests
        oaspeteRepository.save(Oaspete.builder()
                .nume("Ion Popescu")
                .telefon("+40721123456")
                .cnp("1900101123456")
                .dataNastere(LocalDate.of(1990, 1, 1))
                .build());

        oaspeteRepository.save(Oaspete.builder()
                .nume("Maria Ionescu")
                .telefon("+40722654321")
                .cnp("2850515223344")
                .dataNastere(LocalDate.of(1985, 5, 15))
                .build());

        // Employees
        angajatRepository.save(Angajat.builder()
                .nume_prenume("Alexandru Georgescu")
                .functie("Receptioner")
                .salariu(new BigDecimal("4500.00"))
                .data_angajare(LocalDate.of(2022, 3, 15))
                .hotel(h1)
                .build());

        angajatRepository.save(Angajat.builder()
                .nume_prenume("Elena Vasilescu")
                .functie("Manager")
                .salariu(new BigDecimal("7500.00"))
                .data_angajare(LocalDate.of(2020, 1, 10))
                .hotel(h1)
                .build());

        // Services
        serviciuRepository.save(Serviciu.builder()
                .nume("Mic dejun")
                .descriere("Mic dejun tip bufet")
                .cost(50)
                .build());

        serviciuRepository.save(Serviciu.builder()
                .nume("Spa")
                .descriere("Acces zona spa si piscina")
                .cost(150)
                .build());

        serviciuRepository.save(Serviciu.builder()
                .nume("Transfer aeroport")
                .descriere("Transfer de la/la aeroport")
                .cost(100)
                .build());

        log.info("Demo data initialized successfully.");
    }
}
