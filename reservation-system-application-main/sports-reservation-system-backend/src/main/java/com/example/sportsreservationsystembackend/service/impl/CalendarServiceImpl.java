package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.FutureEventsException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.repository.CalendarRepository;
import com.example.sportsreservationsystembackend.rest.mapper.CalendarMapper;
import com.example.sportsreservationsystembackend.rest.mapper.PageMapper;
import com.example.sportsreservationsystembackend.service.CalendarService;
import com.example.sportsreservationsystembackend.service.EventService;
import com.example.sportsreservationsystembackend.service.LocationService;
import com.xstejsk.reservationapp.main.rest.model.CalendarDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarWithEventsDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarsPage;
import com.xstejsk.reservationapp.main.rest.model.CreateCalendarRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateCalendarRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * This class represents calendar service implementation
 *
 * @Author Radim Stejskal
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final LocationService locationService;
    private final CalendarRepository calendarRepository;
    private final EventService eventService;
    private final CalendarMapper calendarMapper;
    private final PageMapper pageMapper;

    /**
     * This method creates new calendar
     * @param createCalendarRequest (required)
     * @return CalendarDTO
     */
    @Override
    public CalendarDTO create(CreateCalendarRequest createCalendarRequest) {
        log.info("Creating calendar with name: {}", createCalendarRequest.getName());
        validateNewCalendar(createCalendarRequest);
        Calendar calendar = calendarMapper.createCalendarRequestToCalendar(createCalendarRequest);
        log.info("Calendar created: {}", calendar);
        return calendarMapper.calendarToCalendarDTO(calendarRepository.save(calendar));
    }

    /**
     * This method deletes calendar
     * @param calendarId (required)
     */
    @Override
    @Transactional
    public void delete(String calendarId) {
        log.info("Deleting calendar with id: {}", calendarId);
        if (!calendarRepository.existsById(calendarId)) {
            throw new ResourceNotFoundException("Calendar with id: " + calendarId + " not found");
        }
       if (eventService.calendarContainsFutureEvents(calendarId)) {
            throw new FutureEventsException("Calendar contains future events");
        }
        calendarRepository.deleteById(calendarId);
        log.info("Calendar with id: {} was deleted", calendarId);
    }

    /**
     * This method gets all calendars
     * @param page page number
     * @param size size of page
     * @param fulltext fulltext search
     * @return page of calendars
     */
    @Override
    public CalendarsPage getAll(Integer page, Integer size, String fulltext) {
        log.info("Getting page {} of size {}", page, size);
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = Integer.MAX_VALUE;
        }
        log.info("Getting page {} of size {} and fulltext {}", page, size, fulltext);
        Specification<Calendar> specification = (root, query, crieriaBuilder) -> {
            if (fulltext != null && !fulltext.isEmpty()) {
                return crieriaBuilder.like(root.get("name"), "%" + fulltext + "%");
            }
            return crieriaBuilder.conjunction();
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Calendar> calendarPage = calendarRepository.findAll(specification, pageable);
        return pageMapper.toCalendarsPage(calendarPage);
    }

    /**
     * This method gets calendar by id
     * @param calendarId (required)
     * @param eventsFrom date from which events will be returned
     * @return calendar with events DTO
     */
    @Override
    public CalendarWithEventsDTO getById(String calendarId, LocalDate eventsFrom, LocalDate eventsTo) {
        log.info("Getting calendar with id: {} and events from {} to {}", calendarId, eventsFrom, eventsTo);
        Calendar calendar = getEntityById(calendarId);
        calendar.setEvents(eventService.getEventsByCalendarId(calendarId, eventsFrom, eventsTo));
        return calendarMapper.calendarToCalendarWithEventsDTO(getEntityById(calendarId));
    }

    /**
     * This method gets calendar by id
     * @param calendarId (required)
     * @return calendar
     */
    @Override
    public Calendar getEntityById(String calendarId) {
        return calendarRepository.findById(calendarId).orElseThrow(
                () -> new ResourceNotFoundException("Calendar with id: " + calendarId + " not found"));
    }

    /**
     * This method updates calendar
     * @param calendarId (required)
     * @param updateCalendarRequest request with updated fields
     * @return calendar DTO
     */
    @Override
    public CalendarDTO update(String calendarId, UpdateCalendarRequest updateCalendarRequest) {
        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow(
                () -> new ResourceNotFoundException("Calendar with id: " + calendarId + " not found"));

        updateNonEmptyFields(calendar, updateCalendarRequest);
        calendarRepository.save(calendar);
        log.info("Calendar with id: {} was updated", calendarId);
        return calendarMapper.calendarToCalendarDTO(calendar);
    }

    /**
     * This method checks if calendar exists
     * @param calendarId
     * @return true if exists, false otherwise
     */
    @Override
    public boolean exists(String calendarId) {
        return calendarRepository.existsById(calendarId);
    }

    private boolean createRequestContainsAllFields(CreateCalendarRequest createCalendarRequest) {
        return createCalendarRequest.getName() != null && !createCalendarRequest.getName().isEmpty();
    }

    private void validateNewCalendar(CreateCalendarRequest createCalendarRequest) {
        if (!createRequestContainsAllFields(createCalendarRequest)) {
            throw new IllegalArgumentException("CreateCalendarRequest does not contain all required fields");
        }
    }

    private void updateNonEmptyFields(Calendar calendar, UpdateCalendarRequest updateCalendarRequest) {
        boolean isUpdated = false;
        if (updateCalendarRequest.getName() != null && !updateCalendarRequest.getName().isEmpty()) {
            calendar.setName(updateCalendarRequest.getName());
            log.info("Calendar name updated to: {}", updateCalendarRequest.getName());
            isUpdated = true;
        }
        if (updateCalendarRequest.getThumbnail() != null) {
            calendar.setThumbnail(updateCalendarRequest.getThumbnail());
            log.info("Calendar thumbnail updated");
            isUpdated = true;
        }
        if (updateCalendarRequest.getLocationId() != null && !updateCalendarRequest.getLocationId().isEmpty()) {
            calendar.setLocation(locationService.findById(updateCalendarRequest.getLocationId()));
            log.info("Calendar location updated to: {}", updateCalendarRequest.getLocationId());
            isUpdated = true;
        }

        if (!isUpdated){
            throw new IllegalArgumentException("No update parameters provided");
        }
    }
}
