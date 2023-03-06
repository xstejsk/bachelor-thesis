package com.rstejskalprojects.reservationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NonPersistentPasswordToken {

    private String token;
    private AppUser user;
    private String password;
}
