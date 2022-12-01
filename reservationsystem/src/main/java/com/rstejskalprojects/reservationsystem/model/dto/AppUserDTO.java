package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import lombok.Data;

@Data
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
    private boolean userIsEnabled;
    @JsonProperty
    private boolean userIsLocked;

    public AppUserDTO(AppUser appUser) {
        this.userId = appUser.getId();
        this.firstName = appUser.getFirstName();
        this.lastName = appUser.getLastName();
        this.email = appUser.getEmail();
        this.userIsEnabled = appUser.getEnabled();
        this.userIsLocked = appUser.getLocked();
    }
}
