package project.scanny.services;

import project.scanny.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String user);
    User createOrLoginUser(User user);
}
