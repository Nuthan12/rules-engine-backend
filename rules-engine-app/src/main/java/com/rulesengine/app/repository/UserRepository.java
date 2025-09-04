package com.rulesengine.app.repository;

import com.rulesengine.model.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their unique username.
     * @param username The username to search for.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUsername(String username);
}

