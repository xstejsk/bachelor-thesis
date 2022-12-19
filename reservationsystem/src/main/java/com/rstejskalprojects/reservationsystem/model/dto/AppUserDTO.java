package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserDTO {
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String fullName;
    @JsonProperty
    private String email;
    @JsonProperty
    private String role;

    public AppUserDTO(AppUser appUser) {
        this.userId = appUser.getId();
        this.fullName = appUser.getFirstName() + " " + appUser.getLastName();
        this.email = appUser.getEmail();
        this.role = appUser.getUserRole().getName();
    }
}
