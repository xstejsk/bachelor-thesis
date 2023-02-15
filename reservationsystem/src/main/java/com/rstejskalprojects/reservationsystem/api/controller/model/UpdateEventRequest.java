package com.rstejskalprojects.reservationsystem.api.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private Integer capacity;
    @JsonProperty
    private Double price;
    @JsonProperty
    private Long locationId;
}
