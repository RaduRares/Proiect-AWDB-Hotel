package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Rol;
import com.hotel.hotel_management.Models.User;
import com.hotel.hotel_management.Repositories.RolRepository;
import com.hotel.hotel_management.Repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/utilizatori")
public class UserController {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public UserController(UserRepository userRepository, RolRepository rolRepository) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        List<Rol> roluri = rolRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roluri", roluri);
        return "utilizatori/list";
    }

    @PostMapping("/{id}/rol")
    public String schimbaRol(@PathVariable Long id,
                              @RequestParam String numRol,
                              RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User negasit"));
        Rol rol = rolRepository.findByNume(numRol)
                .orElseThrow(() -> new RuntimeException("Rol negasit: " + numRol));
        user.setRoluri(Set.of(rol));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Rol actualizat pentru " + user.getUsername());
        return "redirect:/utilizatori";
    }

    @PostMapping("/{id}/delete")
    public String sterge(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User negasit"));
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Utilizatorul " + user.getUsername() + " a fost sters.");
        return "redirect:/utilizatori";
    }
}
