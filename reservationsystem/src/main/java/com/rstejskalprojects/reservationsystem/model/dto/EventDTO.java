package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventDTO {
    @JsonProperty
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime end;
    @JsonProperty
    private Integer maximumCapacity;
    @JsonProperty
    private Double price;
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private RecurrenceGroup recurrenceGroup;
    @JsonProperty
    private Long locationId;
    @JsonProperty
    private Boolean isCanceled = false;
    @JsonProperty
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
        this.isCanceled = event.getIsCanceled();
        this.availableCapacity = event.getAvailableCapacity();
    }
}


