package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This interface represents event repository
 * @author Radim Stejskal
 */

@Repository
public interface EventRepository extends JpaRepository<Event, String>, JpaSpecificationExecutor<Event> {

    @Query("SELECT e FROM Event e " +
            "WHERE e.date = :date " +
            "AND e.calendar.id = :calendarId AND :startTime < e.endTime AND :endTime > e.startTime AND (:eventId IS NULL OR e.id <> :eventId)")
    List<Event> findOverlappingEventsForSingleEvent(@Param("calendarId") String calendarId,
                                                    @Param("eventId") String eventId,
                                                    @Param("date") LocalDate date,
                                                    @Param("startTime") LocalTime startTime,
                                                    @Param("endTime") LocalTime endTime);

    @Query("SELECT e FROM Event e " +
            "WHERE e.date >= :recurringFrom AND e.date <= :recurringTo AND e.dayOfWeek IN :daysOfWeek AND e.calendar.id = :calendarId" +
            " AND :startTime < e.endTime AND :endTime > e.startTime")
    List<Event> findOverlappingEventsForRecurringEvent(@Param("calendarId") String calendarId,
                                                       @Param("recurringFrom") LocalDate recurringFrom,
                                                       @Param("recurringTo") LocalDate recurringTo,
                                                       @Param("startTime") LocalTime startTime,
                                                       @Param("endTime") LocalTime endTime,
                                                       @Param("daysOfWeek") Set<Integer> daysOfWeek);

    List<Event> findAllByCalendarIdAndDateBetweenOrderByDateAsc(String calendarId, LocalDate from, LocalDate to);

    Optional<Event> findByIdAndCalendarId(String eventId, String calendarId);

    List<Event> findAllByRecurrenceGroupId(String recurrenceGroupId);

    @Modifying
    @Query("UPDATE Event e SET e.title = :title, e.description = :description WHERE e.recurrenceGroup.id = :groupId")
    void updateEventSeries(@Param("groupId") String groupId, @Param("title") String title, @Param("description") String description);

    @Query("SELECT min(e.startTime) FROM Event e WHERE e.calendar.id = :calendarId")
    LocalTime findMinStartTimeByCalendarId(String calendarId);

    @Query("SELECT max(e.endTime) FROM Event e WHERE e.calendar.id = :calendarId")
    LocalTime findMaxEndTimeByCalendarId(String calendarId);

    @Query("SELECT e FROM Event e WHERE e.calendar.id = :calendarId AND (e.date > :date OR (e.date = :date AND e.startTime >= :startTime))")
    List<Event> findFutureEventsByCalendar(@Param("calendarId") String calendarId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime);
}
