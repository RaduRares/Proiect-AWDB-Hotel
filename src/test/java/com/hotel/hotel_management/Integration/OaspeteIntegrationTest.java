package com.hotel.hotel_management.Integration;

import com.hotel.hotel_management.Models.Oaspete;
import com.hotel.hotel_management.Services.OaspeteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OaspeteIntegrationTest {

    @Autowired
    private OaspeteService oaspeteService;

    @Test
    void createAndFindOaspete() {
        Oaspete oaspete = Oaspete.builder()
                .nume("Maria Ionescu")
                .telefon("+40720000001")
                .cnp("2900515100001")
                .dataNastere(LocalDate.of(1990, 5, 15))
                .build();

        Oaspete saved = oaspeteService.save(oaspete);
        assertThat(saved.getId()).isNotNull();

        Oaspete found = oaspeteService.findById(saved.getId());
        assertThat(found.getNume()).isEqualTo("Maria Ionescu");
        assertThat(found.getCnp()).isEqualTo("2900515100001");
    }

    @Test
    void updateOaspete_telefon() {
        Oaspete oaspete = Oaspete.builder()
                .nume("Andrei Popa")
                .telefon("+40720000002")
                .build();
        Oaspete saved = oaspeteService.save(oaspete);

        saved.setTelefon("+40731000002");
        oaspeteService.save(saved);

        Oaspete updated = oaspeteService.findById(saved.getId());
        assertThat(updated.getTelefon()).isEqualTo("+40731000002");
    }

    @Test
    void deleteOaspete_thenFindThrowsException() {
        Oaspete oaspete = Oaspete.builder()
                .nume("Oaspete De Sters")
                .telefon("+40720000099")
                .build();
        Oaspete saved = oaspeteService.save(oaspete);
        Long id = saved.getId();

        oaspeteService.deleteById(id);

        assertThatThrownBy(() -> oaspeteService.findById(id))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void searchOaspeti_paginatedAndSorted() {
        oaspeteService.save(Oaspete.builder().nume("Ion Albu").telefon("+40720000011").build());
        oaspeteService.save(Oaspete.builder().nume("Ion Barbu").telefon("+40720000012").build());
        oaspeteService.save(Oaspete.builder().nume("Ana Costea").telefon("+40720000013").build());

        Page<Oaspete> result = oaspeteService.search(
                "Ion",
                PageRequest.of(0, 10, Sort.by("nume").ascending())
        );

        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.getContent().get(0).getNume())
                .isLessThanOrEqualTo(result.getContent().get(1).getNume());
    }
}
