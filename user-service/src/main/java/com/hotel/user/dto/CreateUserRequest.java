package com.hotel.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    @NotBlank
    private String username;
    @Email @NotBlank
    private String email;
    @NotBlank @Size(min = 6)
    private String password;
    private String rol;
}
