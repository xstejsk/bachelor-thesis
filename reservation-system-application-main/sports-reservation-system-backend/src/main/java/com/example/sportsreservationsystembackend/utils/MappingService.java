package com.example.sportsreservationsystembackend.utils;

import com.example.sportsreservationsystembackend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This class represents mapping service implementation used for mapping events
 *
 * @author Radim Stejskal
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MappingService {

    private final EventRepository eventRepository;

    /**
     * This method finds min start time of all events of calendar
     * @param calendarId
     * @return min start time of all events of calendar in format "kk:mm"
     */
    public String findMinTime(String calendarId) {
        LocalTime time = eventRepository.findMinStartTimeByCalendarId(calendarId);
        if (time == null) {
            time = LocalTime.of(8, 0);
        }
        return time.minusMinutes(time.getMinute()).format(DateTimeFormatter.ofPattern("kk:mm"));
    }

    /**
     * This method finds max end time of all events of calendar
     * @param calendarId
     * @return max end time of all events of calendar in format "kk:mm"
     */
    public String findMaxTime(String calendarId) {
        LocalTime time = eventRepository.findMaxEndTimeByCalendarId(calendarId);
        if (time == null) {
            time = LocalTime.of(22, 0);
        }
        return time.plusMinutes(60 - time.getMinute()).format(DateTimeFormatter.ofPattern("kk:mm"));
    }
}
