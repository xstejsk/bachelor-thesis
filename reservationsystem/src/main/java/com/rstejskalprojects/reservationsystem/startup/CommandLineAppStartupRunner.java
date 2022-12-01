package com.rstejskalprojects.reservationsystem.startup;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final String adminEmail = "stejskalrad@gmail.com";


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

