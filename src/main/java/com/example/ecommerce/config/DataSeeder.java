package com.example.ecommerce.config;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String adminEmail = "admin@ecommerce.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping seeding");
            return;
        }

        User adminUser = new User();
        adminUser.setName("Admin");
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode("admin123"));

        userRepository.save(adminUser);

        log.info("==================================================");
        log.info("Admin user created successfully!");
        log.info("Email: {}", adminEmail);
        log.info("Password: admin123");
        log.info("==================================================");
    }
}
