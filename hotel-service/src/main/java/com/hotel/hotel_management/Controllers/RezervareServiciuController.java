package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.RezervareServiciu;
import com.hotel.hotel_management.Services.RezervareServiciuService;
import com.hotel.hotel_management.Services.RezervareService;
import com.hotel.hotel_management.Services.ServiciuService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rezervari/{rezervareId}/servicii")
public class RezervareServiciuController {

    private static final Logger log = LoggerFactory.getLogger(RezervareServiciuController.class);
    private final RezervareServiciuService rezervareServiciuService;
    private final RezervareService rezervareService;
    private final ServiciuService serviciuService;

    public RezervareServiciuController(RezervareServiciuService rezervareServiciuService,
                                       RezervareService rezervareService,
                                       ServiciuService serviciuService) {
        this.rezervareServiciuService = rezervareServiciuService;
        this.rezervareService = rezervareService;
        this.serviciuService = serviciuService;
    }

    @GetMapping("/nou")
    public String createForm(@PathVariable Long rezervareId, Model model) {
        RezervareServiciu rs = new RezervareServiciu();
        rs.setRezervare(rezervareService.findById(rezervareId));
        model.addAttribute("rezervareServiciu", rs);
        model.addAttribute("servicii", serviciuService.findAll());
        model.addAttribute("rezervareId", rezervareId);
        return "rezervare-servicii/form";
    }

    @PostMapping("/nou")
    public String create(@PathVariable Long rezervareId,
                         @Valid @ModelAttribute("rezervareServiciu") RezervareServiciu rs,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("servicii", serviciuService.findAll());
            model.addAttribute("rezervareId", rezervareId);
            return "rezervare-servicii/form";
        }
        rs.setRezervare(rezervareService.findById(rezervareId));
        rezervareServiciuService.save(rs);
        redirectAttributes.addFlashAttribute("success", "Serviciul a fost adaugat la rezervare!");
        return "redirect:/rezervari/" + rezervareId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long rezervareId, @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        rezervareServiciuService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Serviciul a fost eliminat de la rezervare!");
        return "redirect:/rezervari/" + rezervareId;
    }
}
