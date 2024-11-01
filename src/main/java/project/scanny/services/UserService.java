package project.scanny.services;

import project.scanny.models.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findByUsername(String user);
    User createOrLoginUser(User user);
}
