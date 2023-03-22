package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
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
    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="recurrence_group_id")
    private RecurrenceGroup recurrenceGroup;

    @Transient
    private int availableCapacity;

    @PostLoad
    private void calculateAvailableCapacity() {
        try {
            availableCapacity = maximumCapacity - reservations.size();
        } catch (Exception e) {

        }

    }

    public Event(Location location, LocalDateTime startTime, LocalDateTime endTime, Integer maximumCapacity, Double price, String title) {
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumCapacity = maximumCapacity;
        this.price = price;
        this.title = title;
    }

    public Event(LocalDateTime startTime,
                 LocalDateTime endTime,
                 Integer maximumCapacity,
                 Double price,
                 String title,
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


    @PreRemove
    private void removeEventFromRecurrenceGroup() {
        System.out.println("Removing event from recurrence group");
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", location=" + location +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", maximumCapacity=" + maximumCapacity +
                ", price=" + price +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", recurrenceGroup=" + recurrenceGroup +
                ", availableCapacity=" + availableCapacity +
                '}';
    }
}
