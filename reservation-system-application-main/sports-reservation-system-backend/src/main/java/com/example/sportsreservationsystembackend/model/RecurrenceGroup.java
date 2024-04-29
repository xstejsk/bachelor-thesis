package com.example.sportsreservationsystembackend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * This class represents recurrence group entity used for recurring events
 * @author Radim Stejskal
 */

@Entity
@Table(name = "recurrence_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"events"})
@EqualsAndHashCode(exclude = {"events"})
public class RecurrenceGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ElementCollection(fetch = FetchType.EAGER, targetClass=DayOfWeek.class)
    @Column(name="day_of_week")
    private List<DayOfWeek> daysOfWeek;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate repeatUntil;

    @OneToMany(mappedBy = "recurrenceGroup", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Event> events;
}
