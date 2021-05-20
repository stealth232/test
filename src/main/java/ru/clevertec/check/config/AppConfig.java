package ru.clevertec.check.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.clevertec.check.model.user.Role;

import static ru.clevertec.check.service.CheckConstants.ROLE_ADMIN;
import static ru.clevertec.check.service.CheckConstants.ROLE_USER;

@Configuration
public class AppConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    Role roleUser(){
        return new Role(2L, ROLE_USER);
    }

    @Bean
    Role roleAdmin(){
        return new Role(1L, ROLE_ADMIN);
    }
}
