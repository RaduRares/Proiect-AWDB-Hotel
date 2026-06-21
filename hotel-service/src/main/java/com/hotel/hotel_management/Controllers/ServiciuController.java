package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Serviciu;
import com.hotel.hotel_management.Services.ServiciuService;
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
@RequestMapping("/servicii")
public class ServiciuController {

    private static final Logger log = LoggerFactory.getLogger(ServiciuController.class);
    private final ServiciuService serviciuService;

    public ServiciuController(ServiciuService serviciuService) {
        this.serviciuService = serviciuService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Serviciu> pageResult = serviciuService.findAll(PageRequest.of(page, size, sort));
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "servicii/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("serviciu", new Serviciu());
        return "servicii/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("serviciu") Serviciu serviciu,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "servicii/form";
        }
        serviciuService.save(serviciu);
        redirectAttributes.addFlashAttribute("success", "Serviciul a fost creat cu succes!");
        return "redirect:/servicii";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("serviciu", serviciuService.findById(id));
        return "servicii/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("serviciu", serviciuService.findById(id));
        return "servicii/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("serviciu") Serviciu serviciu,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "servicii/form";
        }
        serviciu.setId(id);
        serviciuService.save(serviciu);
        redirectAttributes.addFlashAttribute("success", "Serviciul a fost actualizat cu succes!");
        return "redirect:/servicii";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviciuService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Serviciul a fost sters cu succes!");
        return "redirect:/servicii";
    }
}
