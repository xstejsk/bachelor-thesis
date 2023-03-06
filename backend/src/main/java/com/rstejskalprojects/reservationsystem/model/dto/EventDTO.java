package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventDTO {
    @JsonProperty
    @Min(value = 1, message = "Location id must be greater than 0")
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Future(message = "Event start date cannot be in the past")
    @NotNull(message = "Event start date cannot be null")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Future(message = "Event end date cannot be in the past")
    @NotNull(message = "Event start date cannot be null")
    private LocalDateTime end;
    @JsonProperty
    @Min(value = 1, message = "Maximum capacity must be greater than 0")
    @NotNull(message = "Event start date cannot be null")
    private Integer maximumCapacity;
    @JsonProperty
    @Min(value = 0, message = "Event price cannot be negative")
    @NotNull(message = "Event start date cannot be null")
    private Double price;
    @JsonProperty
    @NotBlank(message = "Event name cannot be blank")
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private RecurrenceGroup recurrenceGroup;
    @JsonProperty
    @Min(value = 1, message = "Location id must be greater than 0")
    @NotNull(message = "Event start date cannot be null")
    private Long locationId;
    @JsonProperty
    @Min(value = 1, message = "Available capacity must be greater than 0")
    private Integer availableCapacity;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.start = event.getStartTime();
        this.end = event.getEndTime();
        this.maximumCapacity = event.getMaximumCapacity();
        this.price = event.getPrice();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.recurrenceGroup = event.getRecurrenceGroup();
        this.locationId = event.getLocation().getId();
        this.availableCapacity = event.getAvailableCapacity();
    }
}


