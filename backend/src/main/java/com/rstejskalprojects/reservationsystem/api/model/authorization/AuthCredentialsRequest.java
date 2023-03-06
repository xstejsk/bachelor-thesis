package com.rstejskalprojects.reservationsystem.api.model.authorization;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AuthCredentialsRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4, max = 12, message = "Password must be between 4 and 12 characters")
    private String password;

}
