package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.Reservation;
import com.xstejsk.reservationapp.main.rest.model.CalendarsPage;
import com.xstejsk.reservationapp.main.rest.model.EventsPage;
import com.xstejsk.reservationapp.main.rest.model.ReservationsPage;
import com.xstejsk.reservationapp.main.rest.model.UsersPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This class represents page mapper
 *
 * @author Radim Stejskal
 */

@Component
@RequiredArgsConstructor
public class PageMapper {

    private final ReservationsMapper reservationsMapper;
    private final EventsMapper eventsMapper;
    private final AppUserMapper usersMapper;
    private final CalendarMapper calendarMapper;

    private <T> T createDtoPage(Page<?> page, Class<T> dtoPageClass) {
        T dtoPage;
        try {
            dtoPage = dtoPageClass.newInstance();
            BeanUtils.copyProperties(page, dtoPage);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error creating DTO page instance", e);
        }
        return dtoPage;
    }

    private <T> void setContent(Object dtoPage, List<T> content) {
        try {
            Method setContentMethod = dtoPage.getClass().getMethod("setContent", List.class);
            setContentMethod.invoke(dtoPage, content);
        } catch (Exception e) {
            throw new RuntimeException("Error setting 'records' property", e);
        }
    }

    /**
     * This method is used for mapping page of reservations to reservations page DTO
     * @param page
     * @return reservations page DTO
     */
    public ReservationsPage toReservationsPage(Page<Reservation> page) {
        ReservationsPage reservationsPage = createDtoPage(page, ReservationsPage.class);
        setContent(reservationsPage, page.getContent().stream().map(reservationsMapper::reservationToReservationDTO).toList());
        return reservationsPage;
    }

    /**
     * This method is used for mapping page of events to events page DTO
     * @param page
     * @return events page DTO
     */
    public EventsPage toEventsPage(Page<Event> page) {
        EventsPage eventsPage = createDtoPage(page, EventsPage.class);
        setContent(eventsPage, page.getContent().stream().map(eventsMapper::eventToEventDTO).toList());
        return eventsPage;
    }

    /**
     * This method is used for mapping page of users to users page DTO
     * @param page
     * @return users page DTO
     */
    public UsersPage toUsersPage(Page<AppUser> page) {
        UsersPage usersPage = createDtoPage(page, UsersPage.class);
        setContent(usersPage, page.getContent().stream().map(usersMapper::appUserToAppUserDTO).toList());
        return usersPage;
    }

    /**
     * This method is used for mapping page of calendars to calendars page DTO
     * @param page
     * @return calendars page DTO
     */
    public CalendarsPage toCalendarsPage(Page<Calendar> page) {
        CalendarsPage calendarsPage = createDtoPage(page, CalendarsPage.class);
        setContent(calendarsPage, page.getContent().stream().map(calendarMapper::calendarToCalendarDTO).toList());
        return calendarsPage;
    }
}