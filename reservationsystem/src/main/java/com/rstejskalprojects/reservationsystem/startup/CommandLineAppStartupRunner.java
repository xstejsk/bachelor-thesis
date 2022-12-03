package com.rstejskalprojects.reservationsystem.startup;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.repository.LocationRepository;
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
    private final String adminEmail = "stejskalrad@gmail.com";
    private final EventRepository eventRepository;

    @Override
    public void run(String...args){
        Location location = new Location(1L, "Sal 1", "nejvetsi sal");
        locationRepository.save(location);
        String password = "pw";
        String encodedPw = bCryptPasswordEncoder.encode(password);
        AppUser admin = new AppUser("Admin",
                "Admin",
                adminEmail,
                encodedPw,
                UserRoleEnum.USER);
        userRepository.save(admin);
        Event event = new Event(LocalDateTime.now(), LocalDateTime.now().plusMinutes(120),
                5, 100d, "Event z databaze", "popis", false, false, null, location);
        eventRepository.save(event);
    }
}

