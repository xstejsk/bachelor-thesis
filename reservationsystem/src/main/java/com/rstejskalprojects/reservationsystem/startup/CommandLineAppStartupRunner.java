package com.rstejskalprojects.reservationsystem.startup;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.repository.LocationRepository;
import com.rstejskalprojects.reservationsystem.repository.RecurrenceGroupRepository;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private UserRepository userRepository;
    private LocationRepository locationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EventRepository eventRepository;
    private final RecurrenceGroupRepository recurrenceGroupRepository;

    @Override
    public void run(String...args){
        Location location = new Location("Sal1", "nejvetsi sal");
        Location location2 = new Location("Sal2", "mensi sal");
        locationRepository.save(location);
        locationRepository.save(location2);
        String password = "admin";
        String encodedPw = bCryptPasswordEncoder.encode(password);
        RecurrenceGroup recurrenceGroup = new RecurrenceGroup(FrequencyEnum.NEVER, List.of(), LocalDate.now());
        recurrenceGroupRepository.save(recurrenceGroup);
        RecurrenceGroup recurrenceGroup1 = recurrenceGroupRepository.findById(1L).get();

        AppUser admin = new AppUser(1L, "Vlastimil", "Novák", "admin", "admin", encodedPw, UserRoleEnum.ADMIN, false, true);
        AppUser user = new AppUser(2L, "Petr", "Jirák", "user", "user", bCryptPasswordEncoder.encode("user"), UserRoleEnum.USER, false, true);
        userRepository.save(admin);
        userRepository.save(user);
        Event event = new Event(LocalDateTime.now(), LocalDateTime.now().plusMinutes(120),
                5, 100d, "Event v Salu 1", "popis", false, recurrenceGroup1, location);
        Event event1 = new Event(LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1),
                5, 100d, "Event v Salu 2", "popis", false, recurrenceGroup1, location2);
        eventRepository.save(event);
        eventRepository.save(event1);

    }
}

