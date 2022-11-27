package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private long id;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("startTime")
    private LocalDateTime startTime;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("endTime")
    private LocalDateTime endTime;
    @NotNull
    @JsonProperty("capacity")
    private int capacity;
    @NotNull
    @JsonProperty("price")
    private float price;
    @NotNull
    @JsonProperty("subject")
    private String subject;
    @NotNull
    @JsonProperty("description")
    private String description;
    @NotNull
    @JsonProperty("isAllDay")
    private boolean isAllDay;
    @Transient
    @JsonIgnore
    private RecurrenceRule recurrenceRule;
    @JsonProperty("recurrenceRule")
    private String recurrenceRuleString(){
        return recurrenceRule == null ? "" : recurrenceRule.toString();
    }
}
