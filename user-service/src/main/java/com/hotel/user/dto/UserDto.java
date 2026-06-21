package com.hotel.user.dto;

import lombok.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private Set<String> roluri;
}
