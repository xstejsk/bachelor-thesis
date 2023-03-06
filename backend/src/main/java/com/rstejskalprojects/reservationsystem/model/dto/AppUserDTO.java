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
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String email;
    @JsonProperty
    private Boolean enabled = false;
    @JsonProperty
    private Boolean locked = false;
    @JsonProperty
    private String role;

    public AppUserDTO(AppUser appUser) {
        this.userId = appUser.getId();
        this.firstName = appUser.getFirstName();
        this.lastName = appUser.getLastName();
        this.enabled = appUser.getEnabled();
        this.locked = appUser.getLocked();
        this.email = appUser.getEmail();
        this.role = appUser.getUserRole().getName();
    }
}
