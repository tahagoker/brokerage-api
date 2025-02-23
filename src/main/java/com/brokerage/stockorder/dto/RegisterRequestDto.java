package com.brokerage.stockorder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    
    @NotBlank(message = "Username cannot be empty")
    private String username;


    @NotBlank(message = "Password cannot be empty")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$",
        message = "Password must be at least 6 characters long and contain at least one digit, " +
                "one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)"
    )
    private String password;
}