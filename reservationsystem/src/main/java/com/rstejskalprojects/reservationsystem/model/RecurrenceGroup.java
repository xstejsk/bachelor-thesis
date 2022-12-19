package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "recurrence_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecurrenceGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Long id;
    @JsonProperty
    private FrequencyEnum frequency;
    @JsonProperty
    @Transient
    private List<DayOfWeek> daysOfWeek;
    @Transient
    @JsonProperty
    private LocalDate endDate;

    public RecurrenceGroup(FrequencyEnum frequency, List<DayOfWeek> daysOfWeek, LocalDate endDate) {
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.endDate = endDate;
    }

    public RecurrenceGroup(FrequencyEnum frequency) {
        this.frequency = frequency;
    }
}
