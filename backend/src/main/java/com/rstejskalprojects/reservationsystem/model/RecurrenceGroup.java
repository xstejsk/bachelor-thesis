package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @Enumerated(value = EnumType.STRING)
    private FrequencyEnum frequency;
    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER, targetClass=DayOfWeek.class)
    @Column(name="day_of_week")
    private List<DayOfWeek> daysOfWeek;
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonIgnore
    @OneToMany(mappedBy = "recurrenceGroup", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Event> events;

    public RecurrenceGroup(FrequencyEnum frequency, List<DayOfWeek> daysOfWeek, LocalDate endDate) {
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.endDate = endDate;
    }

    public RecurrenceGroup(FrequencyEnum frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "RecurrenceGroup{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", daysOfWeek=" + daysOfWeek +
                ", endDate=" + endDate +
                '}';
    }
}
