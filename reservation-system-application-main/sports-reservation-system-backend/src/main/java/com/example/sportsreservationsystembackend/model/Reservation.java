package com.example.sportsreservationsystembackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotNull;

/**
 * This class represents reservation entity
 * @author Radim Stejskal
 */

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="owner_id")
    @NotNull
    private AppUser owner;

    @ManyToOne
    @JoinColumn(name="event_id")
    @NotNull
    private Event event;

    private boolean discountApplied;

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", owner=" + owner +
                ", event=" + event +
                '}';
    }

    public Reservation(AppUser owner, Event event) {
        this.owner = owner;
        this.event = event;
    }
}
