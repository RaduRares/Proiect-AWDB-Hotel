package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Models.Factura;
import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Models.User;
import java.util.List;
import com.hotel.hotel_management.Repositories.UserRepository;
import com.hotel.hotel_management.Services.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
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
    private final FacturaService facturaService;
    private final UserRepository userRepository;

    public RezervareController(RezervareService rezervareService, HotelService hotelService,
                               OaspeteService oaspeteService, TipCameraService tipCameraService,
                               FacturaService facturaService, UserRepository userRepository) {
        this.rezervareService = rezervareService;
        this.hotelService = hotelService;
        this.oaspeteService = oaspeteService;
        this.tipCameraService = tipCameraService;
        this.facturaService = facturaService;
        this.userRepository = userRepository;
    }

    private Hotel getCurrentUserHotel(Authentication auth) {
        if (auth == null) return null;
        return userRepository.findByUsername(auth.getName())
                .map(User::getHotel)
                .orElse(null);
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       @RequestParam(required = false) String status,
                       Authentication auth, Model model) {
        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Hotel userHotel = getCurrentUserHotel(auth);
        Page<Rezervare> pageResult;

        Rezervare.StatusRezervare statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = Rezervare.StatusRezervare.valueOf(status);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
            }
        }

        if (userHotel != null) {
            if (statusEnum != null) {
                pageResult = rezervareService.findByHotelIdAndStatus(
                        userHotel.getId(), statusEnum, PageRequest.of(page, size, sort));
            } else {
                pageResult = rezervareService.findByHotelId(
                        userHotel.getId(), PageRequest.of(page, size, sort));
            }
        } else {
            if (statusEnum != null) {
                pageResult = rezervareService.findByStatus(statusEnum, PageRequest.of(page, size, sort));
            } else {
                pageResult = rezervareService.findAll(PageRequest.of(page, size, sort));
            }
        }

        model.addAttribute("page", pageResult);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("status", status);
        model.addAttribute("statusuri", Rezervare.StatusRezervare.values());
        return "rezervari/list";
    }

    @GetMapping("/nou")
    public String createForm(Authentication auth, Model model) {
        model.addAttribute("rezervare", new Rezervare());
        populateFormDropdowns(auth, model);
        return "rezervari/form";
    }

    @PostMapping("/nou")
    public String create(@Valid @ModelAttribute("rezervare") Rezervare rezervare,
                         BindingResult result, Authentication auth, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDropdowns(auth, model);
            return "rezervari/form";
        }
        Hotel userHotel = getCurrentUserHotel(auth);
        if (userHotel != null) {
            rezervare.setHotel(userHotel);
        }
        try {
            rezervareService.save(rezervare);
            redirectAttributes.addFlashAttribute("success", "Rezervarea a fost creata cu succes!");
            return "redirect:/rezervari";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            populateFormDropdowns(auth, model);
            return "rezervari/form";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("rezervare", rezervareService.findById(id));
        return "rezervari/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication auth, Model model) {
        model.addAttribute("rezervare", rezervareService.findById(id));
        populateFormDropdowns(auth, model);
        return "rezervari/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("rezervare") Rezervare rezervare,
                         BindingResult result, Authentication auth, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDropdowns(auth, model);
            return "rezervari/form";
        }
        rezervare.setId(id);
        Hotel userHotel = getCurrentUserHotel(auth);
        if (userHotel != null) {
            rezervare.setHotel(userHotel);
        }
        try {
            rezervareService.save(rezervare);
            redirectAttributes.addFlashAttribute("success", "Rezervarea a fost actualizata cu succes!");
            return "redirect:/rezervari";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            populateFormDropdowns(auth, model);
            return "rezervari/form";
        }
    }

    @PostMapping("/{id}/schimba-status")
    public String schimbaStatus(@PathVariable Long id,
                                @RequestParam String status,
                                RedirectAttributes redirectAttributes) {
        try {
            Rezervare.StatusRezervare newStatus = Rezervare.StatusRezervare.valueOf(status);
            rezervareService.updateStatus(id, newStatus);

            if (newStatus == Rezervare.StatusRezervare.CHECKED_OUT) {
                facturaService.findByRezervareId(id).orElseGet(() -> {
                    Rezervare rez = rezervareService.findById(id);
                    Factura f = new Factura();
                    f.setRezervare(rez);
                    f.setStatusPlata(Factura.StatusPlata.NEPLATITA);
                    return facturaService.save(f);
                });
                redirectAttributes.addFlashAttribute("success",
                        "Check-out efectuat. Factura a fost generata automat.");
            } else if (newStatus == Rezervare.StatusRezervare.CHECKED_IN) {
                redirectAttributes.addFlashAttribute("success", "Check-in inregistrat cu succes.");
            } else {
                redirectAttributes.addFlashAttribute("success", "Status actualizat: " + newStatus.name());
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Status invalid: " + status);
        } catch (Exception e) {
            log.error("Eroare la schimbarea statusului rezervarii {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Eroare: " + e.getMessage());
        }
        return "redirect:/rezervari";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rezervareService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Rezervarea a fost stearsa cu succes!");
        return "redirect:/rezervari";
    }

    private void populateFormDropdowns(Authentication auth, Model model) {
        Hotel userHotel = getCurrentUserHotel(auth);
        model.addAttribute("userHotel", userHotel);
        if (userHotel != null) {
            model.addAttribute("hoteluri", List.of(userHotel));
            model.addAttribute("tipuriCamere", tipCameraService.findByHotelId(userHotel.getId()));
        } else {
            model.addAttribute("hoteluri", hotelService.findAll());
            model.addAttribute("tipuriCamere", tipCameraService.findAll());
        }
        model.addAttribute("oaspeti", oaspeteService.findAll());
        model.addAttribute("statusuri", Rezervare.StatusRezervare.values());
    }
}
