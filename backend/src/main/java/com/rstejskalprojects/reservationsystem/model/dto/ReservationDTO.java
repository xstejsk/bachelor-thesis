package com.rstejskalprojects.reservationsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservationDTO {
    @JsonProperty
    private Long reservationId;
    @JsonProperty
    @NotNull(message = "The ownerId field cannot be null")
    @Min(value = 1, message = "The ownerId field cannot be empty")
    private Long ownerId;
    @JsonProperty
    @NotNull(message = "The eventId field cannot be null")
    @Min(value = 1, message = "The eventId field cannot be empty")
    private Long eventId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]")
    @JsonProperty
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]")
    @JsonProperty
    private LocalDateTime end;
    @JsonProperty
    private Double price;
    @JsonProperty
    private String title;

    public ReservationDTO(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.ownerId = reservation.getOwner().getId();
        this.eventId = reservation.getEvent().getId();
        this.start = reservation.getEvent().getStartTime();
        this.end = reservation.getEvent().getEndTime();
        this.price = reservation.getEvent().getPrice();
        this.title = reservation.getEvent().getTitle();
    }
}
