package com.hotel.hotel_management.Integration;

import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Services.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HotelIntegrationTest {

    @Autowired
    private HotelService hotelService;

    @Test
    void createAndFindHotel() {
        Hotel hotel = Hotel.builder()
                .nume("Grand Palace Test")
                .stele(5)
                .adresa("Str. Victoriei 10")
                .oras("Bucuresti")
                .build();

        Hotel saved = hotelService.save(hotel);
        assertThat(saved.getId()).isNotNull();

        Hotel found = hotelService.findById(saved.getId());
        assertThat(found.getNume()).isEqualTo("Grand Palace Test");
        assertThat(found.getStele()).isEqualTo(5);
        assertThat(found.getOras()).isEqualTo("Bucuresti");
    }

    @Test
    void updateHotel() {
        Hotel hotel = Hotel.builder()
                .nume("Hotel Initial")
                .stele(3)
                .adresa("Str. Test 1")
                .oras("Cluj")
                .build();
        Hotel saved = hotelService.save(hotel);

        saved.setNume("Hotel Actualizat");
        saved.setStele(4);
        hotelService.save(saved);

        Hotel updated = hotelService.findById(saved.getId());
        assertThat(updated.getNume()).isEqualTo("Hotel Actualizat");
        assertThat(updated.getStele()).isEqualTo(4);
    }

    @Test
    void deleteHotel_thenFindThrowsException() {
        Hotel hotel = Hotel.builder()
                .nume("Hotel De Sters")
                .stele(2)
                .adresa("Str. Test 5")
                .oras("Timisoara")
                .build();
        Hotel saved = hotelService.save(hotel);
        Long id = saved.getId();

        hotelService.deleteById(id);

        assertThatThrownBy(() -> hotelService.findById(id))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void searchHotels_paginatedAndSorted() {
        hotelService.save(Hotel.builder().nume("Alpha Hotel").stele(4).adresa("A").oras("B").build());
        hotelService.save(Hotel.builder().nume("Beta Inn").stele(3).adresa("C").oras("D").build());
        hotelService.save(Hotel.builder().nume("Alpha Resort").stele(5).adresa("E").oras("F").build());

        Page<Hotel> result = hotelService.search(
                "Alpha",
                PageRequest.of(0, 10, Sort.by("stele").descending())
        );

        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.getContent().get(0).getStele())
                .isGreaterThanOrEqualTo(result.getContent().get(1).getStele());
    }
}
