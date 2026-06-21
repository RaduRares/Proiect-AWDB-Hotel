package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Oaspete;
import com.hotel.hotel_management.Repositories.OaspeteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OaspeteServiceTest {

    @Mock
    private OaspeteRepository oaspeteRepository;

    @InjectMocks
    private OaspeteService oaspeteService;

    private Oaspete oaspete;

    @BeforeEach
    void setUp() {
        oaspete = Oaspete.builder()
                .id(1L)
                .nume("Ion Popescu")
                .telefon("+40721000000")
                .dataNastere(LocalDate.of(1990, 5, 15))
                .build();
    }

    @Test
    void findAll_withPageable_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Oaspete> expectedPage = new PageImpl<>(List.of(oaspete));
        when(oaspeteRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Oaspete> result = oaspeteService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(oaspeteRepository).findAll(pageable);
    }

    @Test
    void findAll_noArgs_returnsAllGuests() {
        when(oaspeteRepository.findAll()).thenReturn(List.of(oaspete));

        List<Oaspete> result = oaspeteService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNume()).isEqualTo("Ion Popescu");
    }

    @Test
    void findById_existingId_returnsOaspete() {
        when(oaspeteRepository.findById(1L)).thenReturn(Optional.of(oaspete));

        Oaspete result = oaspeteService.findById(1L);

        assertThat(result.getNume()).isEqualTo("Ion Popescu");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(oaspeteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> oaspeteService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_oaspete_returnsPersistedOaspete() {
        when(oaspeteRepository.save(oaspete)).thenReturn(oaspete);

        Oaspete result = oaspeteService.save(oaspete);

        assertThat(result.getNume()).isEqualTo("Ion Popescu");
        verify(oaspeteRepository).save(oaspete);
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(oaspeteRepository).deleteById(1L);

        oaspeteService.deleteById(1L);

        verify(oaspeteRepository).deleteById(1L);
    }

    @Test
    void search_byName_returnsMatchingPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Oaspete> expectedPage = new PageImpl<>(List.of(oaspete));
        when(oaspeteRepository.findByNumeContainingIgnoreCase("Ion", pageable)).thenReturn(expectedPage);

        Page<Oaspete> result = oaspeteService.search("Ion", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNume()).contains("Ion");
    }
}
