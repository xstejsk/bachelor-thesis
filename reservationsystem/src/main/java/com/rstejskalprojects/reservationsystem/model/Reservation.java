package com.rstejskalprojects.reservationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private AppUser owner;
    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;

    public Reservation(AppUser owner, Event event) {
        this.owner = owner;
        this.event = event;
    }

    @PreRemove
    private void removeReservationFromEvent(){
        System.out.println("Removing reservation from event");
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", owner=" + owner +
                ", event=" + event +
                '}';
    }
}
