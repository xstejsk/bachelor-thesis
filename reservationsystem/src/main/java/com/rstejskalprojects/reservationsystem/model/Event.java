package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    @NotNull //TODO : forbid capacity < 1
    private int capacity;
    @NotNull
    private double price;
    @NotNull
    private String subject;
    @NotNull
    private String description;
    @NotNull
    private boolean isAllDay;
    @Transient
    private boolean isFull = false;
    @Transient
    private RecurrenceRule recurrenceRule;
    @Transient
    @ManyToOne(optional = false)
    @NotNull
    private Location location;
}
