package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findReservationById(Long reservationId);

    @Query("SELECT r FROM Reservation r WHERE r.owner.id = ?1 AND r.event.id = ?2")
    Optional<Reservation> findReservationByEventIdAndUserId(Long userId, Long eventId);

    List<Reservation> findReservationByEventId(Long eventId);

    @Query("SELECT r FROM Reservation r WHERE r.owner.id = ?1 ORDER BY r.event.startTime ASC")
    List<Reservation> findByOwnerId(Long id);

    @Query("SELECT r FROM Reservation r WHERE r.owner.id = ?1 AND r.event.startTime >= CURRENT_TIMESTAMP ORDER BY r.event.startTime ASC")
    List<Reservation> findActivePresentReservationsByUser(Long id);

    @Query("SELECT r FROM Reservation r WHERE r.event.id = ?1 ORDER BY r.event.startTime ASC")
    List<Reservation> findReservationsByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.event.id IN (SELECT e.id FROM Event e WHERE e.recurrenceGroup.id = ?1 AND e.startTime >= CURRENT_TIMESTAMP)")
    void deleteFutureByRecurrenceGroupId(Long recurrenceGroupId);

    @Query("SELECT r FROM Reservation r WHERE r.event.id IN (SELECT e.id FROM Event e WHERE e.recurrenceGroup.id = ?1 AND e.startTime >= CURRENT_TIMESTAMP)")
    Optional<Reservation> findFutureReservationByGroupId(Long groupId);
}
