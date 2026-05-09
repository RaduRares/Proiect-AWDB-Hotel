package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.TipCamera;
import com.hotel.hotel_management.Services.HotelService;
import com.hotel.hotel_management.Services.TipCameraService;
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
@RequestMapping("/tipuri-camere")
public class TipCameraController {

    private static final Logger log = LoggerFactory.getLogger(TipCameraController.class);
    private final TipCameraService tipCameraService;
    private final HotelService hotelService;

    public TipCameraController(TipCameraService tipCameraService, HotelService hotelService) {
        this.tipCameraService = tipCameraService;
        this.hotelService = hotelService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<TipCamera> pageResult = tipCameraService.findAll(PageRequest.of(page, size, sort));
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "tipuri-camere/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("tipCamera", new TipCamera());
        model.addAttribute("hoteluri", hotelService.findAll());
        return "tipuri-camere/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("tipCamera") TipCamera tipCamera,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("hoteluri", hotelService.findAll());
            return "tipuri-camere/form";
        }
        tipCameraService.save(tipCamera);
        redirectAttributes.addFlashAttribute("success", "Tipul de camera a fost creat cu succes!");
        return "redirect:/tipuri-camere";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("tipCamera", tipCameraService.findById(id));
        return "tipuri-camere/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("tipCamera", tipCameraService.findById(id));
        model.addAttribute("hoteluri", hotelService.findAll());
        return "tipuri-camere/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("tipCamera") TipCamera tipCamera,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("hoteluri", hotelService.findAll());
            return "tipuri-camere/form";
        }
        tipCamera.setId(id);
        tipCameraService.save(tipCamera);
        redirectAttributes.addFlashAttribute("success", "Tipul de camera a fost actualizat cu succes!");
        return "redirect:/tipuri-camere";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tipCameraService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Tipul de camera a fost sters cu succes!");
        return "redirect:/tipuri-camere";
    }
}
