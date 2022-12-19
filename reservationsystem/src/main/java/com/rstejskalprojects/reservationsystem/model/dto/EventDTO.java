package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventDTO {
    @JsonProperty
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]")
    private LocalDateTime end;
    @JsonProperty
    private Integer capacity;
    @JsonProperty
    private Double price;
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private Boolean isAllDay = false;
    @JsonProperty
    private RecurrenceGroup recurrenceGroup;
    @JsonProperty
    private Long locationId;
    @JsonProperty
    private Boolean isFull;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.start = event.getStartTime();
        this.end = event.getEndTime();
        this.capacity = event.getCapacity();
        this.price = event.getPrice();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.recurrenceGroup = event.getRecurrenceGroup();
        this.locationId = event.getLocation().getId();
        this.isFull = event.getIsFull();
    }
}


