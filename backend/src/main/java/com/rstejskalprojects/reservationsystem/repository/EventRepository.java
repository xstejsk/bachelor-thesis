package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.location.id = ?1 ORDER BY e.startTime ASC")
    List<Event> findByLocationId(Long id);

//    @Query("SELECT e FROM Event e WHERE e.recurrenceGroup.id = ?1 ORDER BY e.startTime ASC")
//    List<Event> findEventByRecurrenceGroupId(Long groupId);

    List<Event> findByRecurrenceGroupId(Long groupId);

    // find all events that overlap with the given event (StartA <= EndB) and (EndA >= StartB)
    @Query("SELECT e FROM Event e WHERE " +
            "e.location.id = ?2 AND ?3 < e.endTime AND e.startTime < ?4 AND e.id <> ?1")
    List<Event> findOverlappingEvents(Long eventId, Long locationId, LocalDateTime startTime, LocalDateTime endTime);
}
