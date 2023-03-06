package com.rstejskalprojects.reservationsystem.api.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    @Min(value = 1, message = "Maximum capacity must be greater than 0")
    private Integer capacity;
    @JsonProperty
    @Min(value = 0, message = "Event price cannot be negative")
    private Double price;
    @JsonProperty
    private Long locationId;
}
