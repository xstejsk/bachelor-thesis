package com.example.sportsreservationsystembackend.model;

import com.sun.istack.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents event entity
 * @author Radim Stejskal
 */

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"reservations"})
@EqualsAndHashCode(exclude = {"reservations"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne()
    @JoinColumn(name="calendar_id", nullable=false)
    @NotNull
    private Calendar calendar;

    @NotNull
    private LocalDate date;

    private Integer dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Integer maximumCapacity;

    @NotNull
    private Integer price;

    @NotNull
    private Integer discountPrice = 0;

    @NotBlank(message = "Event description cannot be blank")
    private String title;

    private String description;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setEvent(this);
    }

    @ManyToOne
    @JoinColumn(name="recurrence_group_id")
    private RecurrenceGroup recurrenceGroup;

    @Transient
    private int spacesAvailable;

    @PostLoad
    private void calculateAvailableCapacity() {
        try {
            spacesAvailable = maximumCapacity - reservations.size();
        } catch (Exception e) {

        }
    }

    @PrePersist
    private void setDayOfWeek() {
        dayOfWeek = date.getDayOfWeek().getValue();
    }

    public Event(Calendar calendar, LocalDate date, LocalTime startTime, LocalTime endTime, Integer maximumCapacity, Integer price, String title, String description, RecurrenceGroup recurrenceGroup) {
        this.calendar = calendar;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumCapacity = maximumCapacity;
        this.price = price;
        this.title = title;
        this.description = description;
        this.recurrenceGroup = recurrenceGroup;
    }
}
