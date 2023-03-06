package com.rstejskalprojects.reservationsystem.api.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "First name cannot be blank")
    @JsonProperty
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    @JsonProperty
    private String lastName;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4, max = 12, message = "Password must be between 4 and 12 characters")
    @JsonProperty
    private String password;
    @NotBlank(message = "Email cannot be blank")
    @JsonProperty
    @Email(message = "Email should be valid")
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