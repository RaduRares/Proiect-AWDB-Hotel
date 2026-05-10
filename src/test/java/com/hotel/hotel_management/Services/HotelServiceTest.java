package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Repositories.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .id(1L)
                .nume("Grand Hotel")
                .stele(4)
                .adresa("Str. Victoriei 1")
                .oras("Bucuresti")
                .build();
    }

    @Test
    void findAll_withPageable_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Hotel> expectedPage = new PageImpl<>(List.of(hotel));
        when(hotelRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Hotel> result = hotelService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNume()).isEqualTo("Grand Hotel");
        verify(hotelRepository).findAll(pageable);
    }

    @Test
    void findAll_noArgs_returnsAllHotels() {
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<Hotel> result = hotelService.findAll();

        assertThat(result).hasSize(1);
        verify(hotelRepository).findAll();
    }

    @Test
    void findById_existingId_returnsHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        Hotel result = hotelService.findById(1L);

        assertThat(result.getNume()).isEqualTo("Grand Hotel");
        assertThat(result.getOras()).isEqualTo("Bucuresti");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_hotel_returnsPersistedHotel() {
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        Hotel result = hotelService.save(hotel);

        assertThat(result).isEqualTo(hotel);
        verify(hotelRepository).save(hotel);
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(hotelRepository).deleteById(1L);

        hotelService.deleteById(1L);

        verify(hotelRepository).deleteById(1L);
    }

    @Test
    void search_byName_returnsMatchingPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Hotel> expectedPage = new PageImpl<>(List.of(hotel));
        when(hotelRepository.findByNumeContainingIgnoreCase("Grand", pageable)).thenReturn(expectedPage);

        Page<Hotel> result = hotelService.search("Grand", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNume()).contains("Grand");
    }
}
