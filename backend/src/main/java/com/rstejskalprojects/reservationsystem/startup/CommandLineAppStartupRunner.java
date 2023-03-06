package com.rstejskalprojects.reservationsystem.startup;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.repository.LocationRepository;
import com.rstejskalprojects.reservationsystem.repository.RecurrenceGroupRepository;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

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
        try {
            Location location = locationRepository.save(new Location("Sal1", "nejvetsi sal"));
            Location location2 = locationRepository.save(new Location("Sal2", "mensi sal"));
            Event event = new Event(LocalDateTime.now().minusHours(2), LocalDateTime.now().plusMinutes(80),
                    5, 100d, "Event v Salu 1", "popis",  null, locationRepository.findById(location.getId()).get());
            Event event1 = new Event(LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1),
                    5, 100d, "Event v Salu 2", "popis",  null, locationRepository.findById(location2.getId()).get());
            eventRepository.save(event);
            eventRepository.save(event1);


            String password = "admin";
            String encodedPw = bCryptPasswordEncoder.encode(password);
            RecurrenceGroup recurrenceGroup = null;
            //recurrenceGroupRepository.save(recurrenceGroup);
            //RecurrenceGroup recurrenceGroup1 = recurrenceGroupRepository.findById(1L).get();

            AppUser admin = new AppUser(1L, "Vlastimil", "Novák", "admin", "admin", encodedPw, UserRoleEnum.ADMIN, false, true);
            AppUser user = new AppUser(2L, "Petr", "Jirák", "user", "user", bCryptPasswordEncoder.encode("user"), UserRoleEnum.USER, false, true);
            userRepository.save(admin);
            userRepository.save(user);
        } catch (Exception e){
         //   e.printStackTrace();
        }


    }
}

