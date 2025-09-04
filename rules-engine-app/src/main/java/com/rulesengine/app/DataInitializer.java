package com.rulesengine.app;

import com.rulesengine.app.repository.UserRepository;
import com.rulesengine.model.entities.Role;
import com.rulesengine.model.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * This component runs on application startup and creates a set of default users
 * if they don't already exist in the database. This is useful for development and testing.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create an admin, editor, and viewer user with the password "password"
        createDefaultUser("admin", "password", EnumSet.of(Role.ROLE_ADMIN, Role.ROLE_EDITOR, Role.ROLE_VIEWER));
        createDefaultUser("editor", "password", EnumSet.of(Role.ROLE_EDITOR, Role.ROLE_VIEWER));
        createDefaultUser("viewer", "password", EnumSet.of(Role.ROLE_VIEWER));
    }

    private void createDefaultUser(String username, String password, Set<Role> roles) {
        // --- FIX 2 ---
        // Use !userRepository.findByUsername(username).isPresent()
        // This is the universally compatible way to check if an Optional is empty.
        if (!userRepository.findByUsername(username).isPresent()) {
            User user = new User(username, passwordEncoder.encode(password), roles);
            userRepository.save(user);
            System.out.println("Created default user: " + username);
        }
    }
}

