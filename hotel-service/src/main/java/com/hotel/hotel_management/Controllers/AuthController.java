package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Rol;
import com.hotel.hotel_management.Models.User;
import com.hotel.hotel_management.Repositories.RolRepository;
import com.hotel.hotel_management.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Numele de utilizator exista deja!");
            return "redirect:/register";
        }
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Adresa de email este deja folosita!");
            return "redirect:/register";
        }

        Rol userRole = rolRepository.findByNume("USER")
                .orElseThrow(() -> new RuntimeException("Rolul USER nu exista"));

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roluri(Set.of(userRole))
                .build();
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Contul a fost creat cu succes! Va puteti autentifica.");
        return "redirect:/login";
    }
}
