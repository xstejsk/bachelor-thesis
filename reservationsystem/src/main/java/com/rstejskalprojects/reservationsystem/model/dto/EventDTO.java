package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.Event;
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
    private String recurrenceRule;
    @JsonProperty
    private String locationName;
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
        this.isAllDay = event.getIsAllDay();
        // this.recurrenceRule = event.getRecurrenceRule().toString();
        this.locationName = event.getLocation().getName();
        this.isFull = event.getIsFull();
    }
}


