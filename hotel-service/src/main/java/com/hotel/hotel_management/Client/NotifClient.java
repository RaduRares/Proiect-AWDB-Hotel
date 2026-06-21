package com.hotel.hotel_management.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notif-service", fallback = NotifClientFallback.class)
public interface NotifClient {

    @PostMapping("/api/notificari/rezervare-creata")
    void rezervareCreata(@RequestBody NotifEvent event);

    @PostMapping("/api/notificari/factura-emisa")
    void facturaEmisa(@RequestBody NotifEvent event);

    @PostMapping("/api/notificari/rezervare-anulata")
    void rezervareAnulata(@RequestBody NotifEvent event);
}
