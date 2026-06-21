package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Oaspete;
import com.hotel.hotel_management.Services.OaspeteService;
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
@RequestMapping("/oaspeti")
public class OaspeteController {

    private static final Logger log = LoggerFactory.getLogger(OaspeteController.class);
    private final OaspeteService oaspeteService;

    public OaspeteController(OaspeteService oaspeteService) {
        this.oaspeteService = oaspeteService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       @RequestParam(required = false) String search,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Oaspete> pageResult;
        if (search != null && !search.isBlank()) {
            pageResult = oaspeteService.search(search, PageRequest.of(page, size, sort));
        } else {
            pageResult = oaspeteService.findAll(PageRequest.of(page, size, sort));
        }
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        return "oaspeti/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("oaspete", new Oaspete());
        return "oaspeti/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("oaspete") Oaspete oaspete,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "oaspeti/form";
        }
        oaspeteService.save(oaspete);
        redirectAttributes.addFlashAttribute("success", "Oaspetele a fost inregistrat cu succes!");
        return "redirect:/oaspeti";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("oaspete", oaspeteService.findById(id));
        return "oaspeti/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("oaspete", oaspeteService.findById(id));
        return "oaspeti/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("oaspete") Oaspete oaspete,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "oaspeti/form";
        }
        oaspete.setId(id);
        oaspeteService.save(oaspete);
        redirectAttributes.addFlashAttribute("success", "Oaspetele a fost actualizat cu succes!");
        return "redirect:/oaspeti";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        oaspeteService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Oaspetele a fost sters cu succes!");
        return "redirect:/oaspeti";
    }
}
