package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Event;
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

    @Query("SELECT r FROM Reservation r WHERE r.owner.id = ?1 AND r.event.id = ?2 AND r.isCanceled = false ORDER BY r.event.startTime ASC")
    Optional<Reservation> findActiveReservationByEventIdAndUserId(Long userId, Long eventId);

    List<Reservation> findReservationByEventId(Long eventId);

    @Query("SELECT r FROM Reservation r WHERE r.event.recurrenceGroup.id = ?1 ORDER BY r.event.startTime ASC")
    List<Reservation> findReservationByEventGroupId(Long groupId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.isCanceled = TRUE WHERE r.event.id = ?1")
    void cancelReservationsByEventId(Long eventId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.isCanceled = TRUE WHERE r.id = ?1")
    void cancelReservationsById(Long reservationId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.isCanceled = TRUE WHERE r.event.id IN (SELECT e.id FROM Event e WHERE e.recurrenceGroup.id = ?1)")
    void cancelReservationsByEventGroupId(Long groupId);

    @Query("SELECT r FROM Reservation r WHERE r.owner.id = ?1 ORDER BY r.event.startTime ASC")
    List<Reservation> findByOwnerId(Long id);

    @Query("SELECT r FROM Reservation r WHERE r.isCanceled = FALSE AND r.owner.id = ?1 AND r.event.startTime >= CURRENT_TIMESTAMP ORDER BY r.event.startTime ASC")
    List<Reservation> findActivePresentReservationsByUser(Long id);

    @Query("SELECT r FROM Reservation r WHERE r.isCanceled = FALSE AND r.event.id = ?1 ORDER BY r.event.startTime ASC")
    List<Reservation> findActiveReservationsByEventId(Long eventId);
}
