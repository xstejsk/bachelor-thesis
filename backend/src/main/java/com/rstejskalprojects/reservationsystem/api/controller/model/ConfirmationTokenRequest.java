package com.rstejskalprojects.reservationsystem.api.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationTokenRequest {
    @JsonProperty
    @NotBlank
    private String token;
}
