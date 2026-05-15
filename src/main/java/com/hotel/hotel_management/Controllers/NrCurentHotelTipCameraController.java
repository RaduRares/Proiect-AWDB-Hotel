package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.NrCurentHotelTipCamera;
import com.hotel.hotel_management.Services.HotelService;
import com.hotel.hotel_management.Services.NrCurentHotelTipCameraService;
import com.hotel.hotel_management.Services.TipCameraService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventar-camere")
public class NrCurentHotelTipCameraController {

    private static final Logger log = LoggerFactory.getLogger(NrCurentHotelTipCameraController.class);
    private final NrCurentHotelTipCameraService service;
    private final HotelService hotelService;
    private final TipCameraService tipCameraService;

    public NrCurentHotelTipCameraController(NrCurentHotelTipCameraService service,
                                             HotelService hotelService,
                                             TipCameraService tipCameraService) {
        this.service = service;
        this.hotelService = hotelService;
        this.tipCameraService = tipCameraService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("inventar", service.findAll());
        return "inventar-camere/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("entry", new NrCurentHotelTipCamera());
        model.addAttribute("hoteluri", hotelService.findAll());
        model.addAttribute("tipuriCamere", tipCameraService.findAll());
        return "inventar-camere/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("entry") NrCurentHotelTipCamera entry,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("hoteluri", hotelService.findAll());
            model.addAttribute("tipuriCamere", tipCameraService.findAll());
            return "inventar-camere/form";
        }
        service.save(entry);
        redirectAttributes.addFlashAttribute("success", "Inventarul a fost actualizat cu succes!");
        return "redirect:/inventar-camere";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("entry", service.findById(id));
        model.addAttribute("hoteluri", hotelService.findAll());
        model.addAttribute("tipuriCamere", tipCameraService.findAll());
        return "inventar-camere/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("entry") NrCurentHotelTipCamera entry,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("hoteluri", hotelService.findAll());
            model.addAttribute("tipuriCamere", tipCameraService.findAll());
            return "inventar-camere/form";
        }
        entry.setId(id);
        service.save(entry);
        redirectAttributes.addFlashAttribute("success", "Inventarul a fost actualizat cu succes!");
        return "redirect:/inventar-camere";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Inregistrarea a fost stearsa cu succes!");
        return "redirect:/inventar-camere";
    }
}
