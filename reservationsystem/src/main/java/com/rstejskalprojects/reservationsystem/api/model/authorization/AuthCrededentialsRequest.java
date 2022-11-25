package com.rstejskalprojects.reservationsystem.api.model.authorization;

import lombok.Data;

@Data
public class AuthCrededentialsRequest {
    private String username;
    private String password;

}
