package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    @JsonProperty
    private Long reservationId;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String userEmail;
    @JsonProperty
    private String userFullName;
    @JsonProperty
    private Long eventId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty
    private LocalDateTime endTime;
    @JsonProperty
    private Double price;
    @JsonProperty
    private boolean canceled;
    @JsonProperty
    private String subject;

    public ReservationDTO(Reservation reservation) {
        this.userId = reservation.getOwner().getId();
        this.userEmail = reservation.getOwner().getEmail();
        this.userFullName = reservation.getOwner().getFirstName() + reservation.getOwner().getLastName();
        this.eventId = reservation.getEvent().getId();
        this.startTime = reservation.getEvent().getStartTime();
        this.endTime = reservation.getEvent().getEndTime();
        this.price = reservation.getEvent().getPrice();
        this.canceled = reservation.getIsCanceled();
        this.subject = reservation.getEvent().getSubject();
    }
}
