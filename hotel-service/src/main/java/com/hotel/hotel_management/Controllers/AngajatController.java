package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Angajat;
import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Services.AngajatService;
import com.hotel.hotel_management.Services.HotelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/angajati")
public class AngajatController {

    private static final Logger log = LoggerFactory.getLogger(AngajatController.class);
    private final AngajatService angajatService;
    private final HotelService hotelService;

    public AngajatController(AngajatService angajatService, HotelService hotelService) {
        this.angajatService = angajatService;
        this.hotelService = hotelService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Angajat> pageResult = angajatService.findAll(PageRequest.of(page, size, sort));
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "angajati/list";
    }

    @GetMapping("/nou")
    public String createForm(@RequestParam(required = false) Long hotelId, Model model) {
        Angajat angajat = new Angajat();
        if (hotelId != null) {
            Hotel h = hotelService.findById(hotelId);
            angajat.setHotel(h);
            model.addAttribute("hotelFixat", h);
        }
        model.addAttribute("angajat", angajat);
        model.addAttribute("hoteluri", hotelService.findAll());
        model.addAttribute("returnHotelId", hotelId);
        return "angajati/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("angajat") Angajat angajat,
                         BindingResult result, Model model,
                         @RequestParam(required = false) Long returnHotelId,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("hoteluri", hotelService.findAll());
            model.addAttribute("returnHotelId", returnHotelId);
            return "angajati/form";
        }
        Angajat saved = angajatService.save(angajat);
        angajatService.autoCreateUser(saved).ifPresentOrElse(
                cred -> {
                    redirectAttributes.addFlashAttribute("success", "Angajatul a fost adaugat cu succes!");
                    redirectAttributes.addFlashAttribute("info", "Cont creat automat — " + cred);
                },
                () -> redirectAttributes.addFlashAttribute("success", "Angajatul a fost adaugat cu succes!")
        );
        if (returnHotelId != null) {
            return "redirect:/hoteluri/" + returnHotelId;
        }
        return "redirect:/angajati";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("angajat", angajatService.findById(id));
        return "angajati/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("angajat", angajatService.findById(id));
        model.addAttribute("hoteluri", hotelService.findAll());
        return "angajati/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("angajat") Angajat angajat,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("hoteluri", hotelService.findAll());
            return "angajati/form";
        }
        angajat.setId(id);
        angajatService.save(angajat);
        redirectAttributes.addFlashAttribute("success", "Angajatul a fost actualizat cu succes!");
        return "redirect:/angajati";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        angajatService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Angajatul a fost sters cu succes!");
        return "redirect:/angajati";
    }
}
