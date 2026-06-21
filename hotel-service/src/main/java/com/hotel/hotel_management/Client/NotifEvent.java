package com.hotel.hotel_management.Client;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotifEvent {
    private String tip;
    private String mesaj;
    private Long referintaId;
}
