package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // find all by location name order by start date asc
    @Query("SELECT e FROM Event e WHERE e.location.name = ?1 ORDER BY e.startTime ASC")
    List<Event> findByLocationName(String name);

    @Query("SELECT e FROM Event e WHERE e.location.id = ?1 ORDER BY e.startTime ASC")
    List<Event> findByLocationId(Long id);

    @Query("SELECT e FROM Event e WHERE e.recurrenceGroup.id = ?1 ORDER BY e.startTime ASC")
    List<Event> findEventByRecurrenceGroupId(Long groupId);

    @Query("SELECT e FROM Event e WHERE e.isCanceled = FALSE ORDER BY e.startTime ASC")
    List<Event> findAllNonCanceled();

    @Query("SELECT e FROM Event e WHERE e.isCanceled = FALSE AND e.location.id = ?1 ORDER BY e.startTime ASC")
    List<Event> findActiveByLocationId(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event e SET e.isCanceled = TRUE WHERE e.id = ?1")
    void cancelEventById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event e SET e.isCanceled = TRUE WHERE e.recurrenceGroup.id = ?1")
    void cancelEventByGroupId(Long groupId);

    // find all events that overlap with the given event (StartA <= EndB) and (EndA >= StartB)
    @Query("SELECT e FROM Event e WHERE e.isCanceled = FALSE AND " +
            "e.location.id = ?2 AND ?3 < e.endTime AND e.startTime < ?4 AND e.id <> ?1")
    List<Event> findOverlappingEvents(Long eventId, Long locationId, LocalDateTime startTime, LocalDateTime endTime);
}
