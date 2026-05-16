package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Models.Rol;
import com.hotel.hotel_management.Models.User;
import com.hotel.hotel_management.Repositories.HotelRepository;
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
    private final HotelRepository hotelRepository;

    public UserController(UserRepository userRepository, RolRepository rolRepository,
                          HotelRepository hotelRepository) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.hotelRepository = hotelRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        List<Rol> roluri = rolRepository.findAll();
        List<Hotel> hoteluri = hotelRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roluri", roluri);
        model.addAttribute("hoteluri", hoteluri);
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

    @PostMapping("/{id}/hotel")
    public String asigneazaHotel(@PathVariable Long id,
                                  @RequestParam(required = false) Long hotelId,
                                  RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User negasit"));
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
                .orElseThrow(() -> new RuntimeException("User negasit"));
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Utilizatorul " + user.getUsername() + " a fost sters.");
        return "redirect:/utilizatori";
    }
}
