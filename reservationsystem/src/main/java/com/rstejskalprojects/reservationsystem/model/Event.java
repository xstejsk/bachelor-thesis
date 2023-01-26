package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name="locaton_id")
    @NotNull
    private Location location;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    @NotNull
    private Integer capacity;
    @NotNull
    private Double price;
    @NotNull
    private String title;
    private String description;
    @Transient
    private Boolean isFull = false;
    @ManyToOne
    @JoinColumn(name="recurrence_group_id")
    private RecurrenceGroup recurrenceGroup;
    private Boolean isCanceled = false;

    public Event(LocalDateTime startTime,
                 LocalDateTime endTime, Integer capacity,
                 Double price, String title,
                 String description,
                 Boolean isFull,
                 RecurrenceGroup recurrenceGroup,
                 Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.price = price;
        this.title = title;
        this.description = description;
        this.isFull = isFull;
        this.recurrenceGroup = recurrenceGroup;
        this.location = location;
    }

    public Event(LocalDateTime startTime,
                 LocalDateTime endTime, Integer capacity,
                 Double price, String title,
                 String description,
                 Boolean isFull,
                 RecurrenceGroup recurrenceGroup,
                 Boolean isCanceled,
                 Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.price = price;
        this.title = title;
        this.description = description;
        this.isFull = isFull;
        this.recurrenceGroup = recurrenceGroup;
        this.isCanceled = isCanceled;
        this.location = location;
    }
}
