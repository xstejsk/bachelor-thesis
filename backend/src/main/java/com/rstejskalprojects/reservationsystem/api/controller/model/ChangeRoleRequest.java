package com.rstejskalprojects.reservationsystem.api.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {

    @JsonProperty
    @NotBlank
    private String role;
}
