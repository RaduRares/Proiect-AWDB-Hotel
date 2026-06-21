package com.hotel.hotel_management.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotifClientFallback implements NotifClient {

    private static final Logger log = LoggerFactory.getLogger(NotifClientFallback.class);

    @Override
    public void rezervareCreata(NotifEvent event) {
        log.warn("Notif-service indisponibil. Notificare rezervare-creata pierduta: id={}", event.getReferintaId());
    }

    @Override
    public void facturaEmisa(NotifEvent event) {
        log.warn("Notif-service indisponibil. Notificare factura-emisa pierduta: id={}", event.getReferintaId());
    }

    @Override
    public void rezervareAnulata(NotifEvent event) {
        log.warn("Notif-service indisponibil. Notificare rezervare-anulata pierduta: id={}", event.getReferintaId());
    }
}
