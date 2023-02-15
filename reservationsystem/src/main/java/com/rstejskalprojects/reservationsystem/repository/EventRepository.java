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

    List<Event> findByLocationName(String name);

    List<Event> findByLocationId(Long id);

    List<Event> findEventByRecurrenceGroupId(Long groupId);

    @Query("SELECT e FROM Event e WHERE e.isCanceled = FALSE")
    List<Event> findAllNonCanceled();

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
