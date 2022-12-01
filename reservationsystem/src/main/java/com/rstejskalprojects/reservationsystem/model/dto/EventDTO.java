package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.Event;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    @JsonProperty
    private long eventId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    @JsonProperty
    private int capacity;
    @JsonProperty
    private double price;
    @JsonProperty
    private String subject;
    @JsonProperty
    private String description;
    @JsonProperty
    private boolean isAllDay;
    @JsonProperty
    private String recurrenceRule;
    @JsonProperty
    private String locationName;
    @JsonProperty
    private boolean isFull;

    public EventDTO(Event event) {
        this.eventId = event.getId();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.capacity = event.getCapacity();
        this.price = event.getPrice();
        this.subject = event.getSubject();
        this.description = event.getDescription();
        this.isAllDay = event.isAllDay();
        this.recurrenceRule = event.getRecurrenceRule().toString();
        this.locationName = event.getLocation().getName();
        this.isFull = event.isFull();
    }
}


