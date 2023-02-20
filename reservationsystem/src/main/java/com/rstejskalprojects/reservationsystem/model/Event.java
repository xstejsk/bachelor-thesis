package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;
    @NotNull
    private Integer maximumCapacity;
    @NotNull
    private Double price;
    @NotNull
    private String title;
    private String description;
    @OneToMany(mappedBy = "event")
    private List<Reservation> reservations;
    @ManyToOne
    @JoinColumn(name="recurrence_group_id")
    private RecurrenceGroup recurrenceGroup;
    private Boolean isCanceled = false;

    @Transient
    private int availableCapacity;

    @PostLoad
    private void calculateAvailableCapacity() {
        availableCapacity = maximumCapacity - reservations.size();
    }

    public Event(LocalDateTime startTime,
                 LocalDateTime endTime, Integer maximumCapacity,
                 Double price, String title,
                 String description,
                 RecurrenceGroup recurrenceGroup,
                 Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumCapacity = maximumCapacity;
        this.price = price;
        this.title = title;
        this.description = description;
        this.recurrenceGroup = recurrenceGroup;
        this.location = location;
    }

    public Event(LocalDateTime startTime,
                 LocalDateTime endTime, Integer maximumCapacity,
                 Double price, String title,
                 String description,
                 RecurrenceGroup recurrenceGroup,
                 Boolean isCanceled,
                 Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumCapacity = maximumCapacity;
        this.price = price;
        this.title = title;
        this.description = description;
        this.recurrenceGroup = recurrenceGroup;
        this.isCanceled = isCanceled;
        this.location = location;
    }
}
