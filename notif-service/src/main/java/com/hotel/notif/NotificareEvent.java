package com.hotel.notif;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NotificareEvent {
    private String tip;
    private String mesaj;
    private Long referintaId;
}
