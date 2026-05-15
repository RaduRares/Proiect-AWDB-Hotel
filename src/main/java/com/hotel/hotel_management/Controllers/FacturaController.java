package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Factura;
import com.hotel.hotel_management.Services.FacturaService;
import com.hotel.hotel_management.Services.RezervareService;
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
@RequestMapping("/facturi")
public class FacturaController {

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);
    private final FacturaService facturaService;
    private final RezervareService rezervareService;

    public FacturaController(FacturaService facturaService, RezervareService rezervareService) {
        this.facturaService = facturaService;
        this.rezervareService = rezervareService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Factura> pageResult = facturaService.findAll(PageRequest.of(page, size, sort));
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "facturi/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("factura", new Factura());
        model.addAttribute("rezervari", rezervareService.findAll(PageRequest.of(0, 100)).getContent());
        model.addAttribute("statusuri", Factura.StatusPlata.values());
        return "facturi/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("factura") Factura factura,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("rezervari", rezervareService.findAll(PageRequest.of(0, 100)).getContent());
            model.addAttribute("statusuri", Factura.StatusPlata.values());
            return "facturi/form";
        }
        facturaService.save(factura);
        redirectAttributes.addFlashAttribute("success", "Factura a fost creata cu succes!");
        return "redirect:/facturi";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("factura", facturaService.findById(id));
        return "facturi/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("factura", facturaService.findById(id));
        model.addAttribute("rezervari", rezervareService.findAll(PageRequest.of(0, 100)).getContent());
        model.addAttribute("statusuri", Factura.StatusPlata.values());
        return "facturi/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("factura") Factura factura,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("rezervari", rezervareService.findAll(PageRequest.of(0, 100)).getContent());
            model.addAttribute("statusuri", Factura.StatusPlata.values());
            return "facturi/form";
        }
        factura.setId(id);
        facturaService.save(factura);
        redirectAttributes.addFlashAttribute("success", "Factura a fost actualizata cu succes!");
        return "redirect:/facturi";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        facturaService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Factura a fost stearsa cu succes!");
        return "redirect:/facturi";
    }
}
