package com.hotel.notif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificari")
public class NotificareController {

    private static final Logger log = LoggerFactory.getLogger(NotificareController.class);
    private final List<Map<String, Object>> istoric = new ArrayList<>();

    @PostMapping("/rezervare-creata")
    public ResponseEntity<Void> rezervareCreata(@RequestBody NotificareEvent event) {
        log.info("[NOTIFICARE] Rezervare noua creata: id={}, mesaj={}", event.getReferintaId(), event.getMesaj());
        istoric.add(Map.of(
            "tip", "REZERVARE_CREATA",
            "referintaId", event.getReferintaId(),
            "mesaj", event.getMesaj(),
            "timestamp", LocalDateTime.now().toString()
        ));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/factura-emisa")
    public ResponseEntity<Void> facturaEmisa(@RequestBody NotificareEvent event) {
        log.info("[NOTIFICARE] Factura emisa: id={}, mesaj={}", event.getReferintaId(), event.getMesaj());
        istoric.add(Map.of(
            "tip", "FACTURA_EMISA",
            "referintaId", event.getReferintaId(),
            "mesaj", event.getMesaj(),
            "timestamp", LocalDateTime.now().toString()
        ));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rezervare-anulata")
    public ResponseEntity<Void> rezervareAnulata(@RequestBody NotificareEvent event) {
        log.info("[NOTIFICARE] Rezervare anulata: id={}", event.getReferintaId());
        istoric.add(Map.of(
            "tip", "REZERVARE_ANULATA",
            "referintaId", event.getReferintaId(),
            "mesaj", event.getMesaj(),
            "timestamp", LocalDateTime.now().toString()
        ));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/istoric")
    public List<Map<String, Object>> getIstoric() {
        return istoric;
    }
}
