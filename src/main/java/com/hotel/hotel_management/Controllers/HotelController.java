package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Hotel;
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
@RequestMapping("/hoteluri")
public class HotelController {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       @RequestParam(required = false) String search,
                       Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Hotel> pageResult;
        if (search != null && !search.isBlank()) {
            pageResult = hotelService.search(search, PageRequest.of(page, size, sort));
        } else {
            pageResult = hotelService.findAll(PageRequest.of(page, size, sort));
        }
        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        return "hoteluri/list";
    }

    @GetMapping("/nou")
    public String createForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "hoteluri/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("hotel") Hotel hotel,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "hoteluri/form";
        }
        hotelService.save(hotel);
        redirectAttributes.addFlashAttribute("success", "Hotelul a fost creat cu succes!");
        return "redirect:/hoteluri";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("hotel", hotelService.findById(id));
        return "hoteluri/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("hotel", hotelService.findById(id));
        return "hoteluri/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("hotel") Hotel hotel,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "hoteluri/form";
        }
        hotel.setId(id);
        hotelService.save(hotel);
        redirectAttributes.addFlashAttribute("success", "Hotelul a fost actualizat cu succes!");
        return "redirect:/hoteluri";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hotelService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Hotelul a fost sters cu succes!");
        return "redirect:/hoteluri";
    }
}
