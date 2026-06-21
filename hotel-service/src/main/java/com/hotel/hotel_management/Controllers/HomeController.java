package com.hotel.hotel_management.Controllers;

import com.hotel.hotel_management.Services.HotelService;
import com.hotel.hotel_management.Services.OaspeteService;
import com.hotel.hotel_management.Services.RezervareService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HotelService hotelService;
    private final RezervareService rezervareService;
    private final OaspeteService oaspeteService;

    public HomeController(HotelService hotelService, RezervareService rezervareService, OaspeteService oaspeteService) {
        this.hotelService = hotelService;
        this.rezervareService = rezervareService;
        this.oaspeteService = oaspeteService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalHoteluri", hotelService.findAll(PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("totalRezervari", rezervareService.findAll(PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("totalOaspeti", oaspeteService.findAll(PageRequest.of(0, 1)).getTotalElements());
        return "index";
    }
}
