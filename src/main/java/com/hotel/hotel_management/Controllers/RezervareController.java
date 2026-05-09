package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Services.*;
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
@RequestMapping("/rezervari")
public class RezervareController {

    private static final Logger log = LoggerFactory.getLogger(RezervareController.class);
    private final RezervareService rezervareService;
    private final HotelService hotelService;
    private final OaspeteService oaspeteService;
    private final TipCameraService tipCameraService;

    public RezervareController(RezervareService rezervareService, HotelService hotelService,
                               OaspeteService oaspeteService, TipCameraService tipCameraService) {
        this.rezervareService = rezervareService;
        this.hotelService = hotelService;
        this.oaspeteService = oaspeteService;
        this.tipCameraService = tipCameraService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       @RequestParam(required = false) String status,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Rezervare> pageResult;
        if (status != null && !status.isBlank()) {
            pageResult = rezervareService.findByStatus(
                    Rezervare.StatusRezervare.valueOf(status), PageRequest.of(page, size, sort));
        } else {
            pageResult = rezervareService.findAll(PageRequest.of(page, size, sort));
        }
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("status", status);
        model.addAttribute("statusuri", Rezervare.StatusRezervare.values());
        return "rezervari/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("rezervare", new Rezervare());
        populateFormDropdowns(model);
        return "rezervari/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("rezervare") Rezervare rezervare,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDropdowns(model);
            return "rezervari/form";
        }
        rezervareService.save(rezervare);
        redirectAttributes.addFlashAttribute("success", "Rezervarea a fost creata cu succes!");
        return "redirect:/rezervari";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("rezervare", rezervareService.findById(id));
        return "rezervari/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("rezervare", rezervareService.findById(id));
        populateFormDropdowns(model);
        return "rezervari/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("rezervare") Rezervare rezervare,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDropdowns(model);
            return "rezervari/form";
        }
        rezervare.setId(id);
        rezervareService.save(rezervare);
        redirectAttributes.addFlashAttribute("success", "Rezervarea a fost actualizata cu succes!");
        return "redirect:/rezervari";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rezervareService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Rezervarea a fost stearsa cu succes!");
        return "redirect:/rezervari";
    }

    private void populateFormDropdowns(Model model) {
        model.addAttribute("hoteluri", hotelService.findAll());
        model.addAttribute("oaspeti", oaspeteService.findAll());
        model.addAttribute("tipuriCamere", tipCameraService.findAll());
        model.addAttribute("statusuri", Rezervare.StatusRezervare.values());
    }
}
