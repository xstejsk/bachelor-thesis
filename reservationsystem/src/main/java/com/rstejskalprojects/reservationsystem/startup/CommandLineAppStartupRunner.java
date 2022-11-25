package com.rstejskalprojects.reservationsystem.startup;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * configure this field yourself
     */
    private final String adminEmail = "stejskalrad@gmail.com";

    /**
     * Creates an admin account after running the application for the first time, it is HIGHLY
     * advised to change the admin's password after running the application
     * @param args application arguments
     */
    @Override
    public void run(String...args){
        String password = "pw";
        String encodedPw = bCryptPasswordEncoder.encode(password);
        AppUser admin = new AppUser(1L, "Admin",
                "Admin",
                adminEmail, adminEmail,
                encodedPw,
                UserRoleEnum.USER, false, true);
        userRepository.save(admin);
    }
}

