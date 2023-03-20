package com.rstejskalprojects.reservationsystem.api.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BanstatusRequest {
    @JsonProperty
    @NotNull
    private Boolean banned;
}
