package com.example.ecommerce.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private String email;

    public LoginResponseDTO(String token, String email) {
        this.token = token;
        this.email = email;
    }
}
