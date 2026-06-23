package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Models.Rol;
import com.hotel.hotel_management.Models.User;
import com.hotel.hotel_management.Repositories.HotelRepository;
import com.hotel.hotel_management.Repositories.RolRepository;
import com.hotel.hotel_management.Repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/utilizatori")
public class UserController {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RolRepository rolRepository,
                          HotelRepository hotelRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.hotelRepository = hotelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        List<Rol> roluri = rolRepository.findAll().stream()
                .filter(r -> !"ADMINISTRATOR".equals(r.getNume()))
                .toList();
        Set<Long> administratorIds = users.stream()
                .filter(u -> u.getRoluri().stream().anyMatch(r -> "ADMINISTRATOR".equals(r.getNume())))
                .map(User::getId)
                .collect(java.util.stream.Collectors.toSet());
        model.addAttribute("users", users);
        model.addAttribute("roluri", roluri);
        model.addAttribute("hoteluri", hotelRepository.findAll());
        model.addAttribute("administratorIds", administratorIds);
        return "utilizatori/list";
    }

    @PostMapping("/{id}/rol")
    public String schimbaRol(@PathVariable Long id,
                              @RequestParam String numRol,
                              RedirectAttributes redirectAttributes) {
        if ("ADMINISTRATOR".equals(numRol)) {
            redirectAttributes.addFlashAttribute("error", "Nu poti asigna rolul ADMINISTRATOR.");
            return "redirect:/utilizatori";
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        boolean isAdministrator = user.getRoluri().stream()
                .anyMatch(r -> "ADMINISTRATOR".equals(r.getNume()));
        if (isAdministrator) {
            redirectAttributes.addFlashAttribute("error", "Contul ADMINISTRATOR nu poate fi modificat.");
            return "redirect:/utilizatori";
        }
        Rol rol = rolRepository.findByNume(numRol)
                .orElseThrow(() -> new ResourceNotFoundException("Rol negasit: " + numRol));
        Set<Rol> nouSet = new HashSet<>();
        nouSet.add(rol);
        user.setRoluri(nouSet);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Rol actualizat pentru " + user.getUsername());
        return "redirect:/utilizatori";
    }

    @PostMapping("/{id}/hotel")
    public String asigneazaHotel(@PathVariable Long id,
                                  @RequestParam(required = false) Long hotelId,
                                  RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        Hotel hotel = (hotelId != null) ? hotelRepository.findById(hotelId).orElse(null) : null;
        user.setHotel(hotel);
        userRepository.save(user);
        String hotelNume = hotel != null ? hotel.getNume() : "niciunul";
        redirectAttributes.addFlashAttribute("success",
                "Hotel actualizat pentru " + user.getUsername() + ": " + hotelNume);
        return "redirect:/utilizatori";
    }

    @PostMapping("/{id}/delete")
    public String sterge(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        boolean isAdministrator = user.getRoluri().stream()
                .anyMatch(r -> "ADMINISTRATOR".equals(r.getNume()));
        if (isAdministrator) {
            redirectAttributes.addFlashAttribute("error", "Contul ADMINISTRATOR nu poate fi sters.");
            return "redirect:/utilizatori";
        }
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Utilizatorul " + user.getUsername() + " a fost sters.");
        return "redirect:/utilizatori";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roluri", rolRepository.findAll().stream()
                .filter(r -> !"ADMINISTRATOR".equals(r.getNume())).toList());
        model.addAttribute("hoteluri", hotelRepository.findAll());
        return "utilizatori/form";
    }

    @PostMapping("/nou")
    public String create(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String numRol,
                         @RequestParam(required = false) Long hotelId,
                         RedirectAttributes redirectAttributes) {
        if ("ADMINISTRATOR".equals(numRol)) {
            redirectAttributes.addFlashAttribute("error", "Nu poti asigna rolul ADMINISTRATOR.");
            return "redirect:/utilizatori/nou";
        }
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username '" + username + "' exista deja.");
            return "redirect:/utilizatori/nou";
        }
        Rol rol = rolRepository.findByNume(numRol)
                .orElseThrow(() -> new ResourceNotFoundException("Rol negasit: " + numRol));
        Hotel hotel = (hotelId != null) ? hotelRepository.findById(hotelId).orElse(null) : null;
        userRepository.save(User.builder()
                .username(username).email(email)
                .password(passwordEncoder.encode(password))
                .roluri(Set.of(rol)).hotel(hotel).build());
        redirectAttributes.addFlashAttribute("success", "Utilizatorul '" + username + "' a fost creat.");
        return "redirect:/utilizatori";
    }

    @GetMapping("/{id}/schimba-parola")
    public String schimbaParolaForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
        return "utilizatori/schimba-parola";
    }

    @PostMapping("/{id}/schimba-parola")
    public String schimbaParola(@PathVariable Long id,
                                @RequestParam String parolaNoua,
                                @RequestParam String confirmare,
                                RedirectAttributes redirectAttributes) {
        if (!parolaNoua.equals(confirmare)) {
            redirectAttributes.addFlashAttribute("error", "Parolele nu coincid.");
            return "redirect:/utilizatori/" + id + "/schimba-parola";
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setPassword(passwordEncoder.encode(parolaNoua));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Parola a fost schimbata.");
        return "redirect:/utilizatori";
    }

    @GetMapping("/profil/schimba-parola")
    public String profilSchimbaParolaForm(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
        model.addAttribute("user", user);
        return "utilizatori/schimba-parola";
    }

    @PostMapping("/profil/schimba-parola")
    public String profilSchimbaParola(Authentication auth,
                                      @RequestParam String parolaNoua,
                                      @RequestParam String confirmare,
                                      RedirectAttributes redirectAttributes) {
        if (!parolaNoua.equals(confirmare)) {
            redirectAttributes.addFlashAttribute("error", "Parolele nu coincid.");
            return "redirect:/utilizatori/profil/schimba-parola";
        }
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
        user.setPassword(passwordEncoder.encode(parolaNoua));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Parola ta a fost schimbata.");
        return "redirect:/";
    }
}
