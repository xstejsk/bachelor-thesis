package com.rstejskalprojects.reservationsystem.api.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequest {

    @NotNull
    @JsonProperty
    private String firstName;
    @NotNull
    @JsonProperty
    private String lastName;
    @NotNull
    @JsonProperty
    private String password;
    @NotNull
    @JsonProperty
    @Column(unique = true)
    private String email;

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}