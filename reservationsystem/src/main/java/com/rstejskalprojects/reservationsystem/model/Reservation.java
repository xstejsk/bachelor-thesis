package com.rstejskalprojects.reservationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    @ManyToOne(optional = false)
    private AppUser owner;
    @ManyToOne(optional = false)
    private Event event;
    private Boolean isCanceled;

    public Reservation(AppUser owner, Event event, Boolean isCanceled) {
        this.owner = owner;
        this.event = event;
        this.isCanceled = isCanceled;
    }
}
