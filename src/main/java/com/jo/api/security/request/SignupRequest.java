package com.jo.api.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    @Email
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,}", message = "Le mot de passe doit contenir 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.")
    private String password;

    private Set<String> role;

}
