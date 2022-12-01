package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findReservationById(Long reservationId);

    @Query("SELECT r FROM Reservation r JOIN AppUser u ON u.id = r.owner.id AND u.username = ?1")
    Collection<Reservation> findReservationByOwnerUsername(String username);

    Collection<Reservation> findReservationByEventId(Long eventId);

}
