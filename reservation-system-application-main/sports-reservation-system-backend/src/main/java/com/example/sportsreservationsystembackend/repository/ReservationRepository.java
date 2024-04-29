package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * This interface represents reservation repository
 * @author Radim Stejskal
 */

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String>, JpaSpecificationExecutor<Reservation> {

    boolean existsByEventIdAndAndOwnerId(String eventId, String ownerId);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.event.calendar.id = :calendarId " +
            "AND r.owner.id = :ownerId " +
            "AND r.event.date >= :from " +
            "ORDER BY r.event.date ASC")
    Page<Reservation> findAllByFilters(@Param("from") LocalDate from, @Param("calendarId") String calendarId, @Param("ownerId") String ownerId, Pageable pageable);

    boolean existsByOwnerIdAndEventDate(String ownerId, LocalDate date);
}
