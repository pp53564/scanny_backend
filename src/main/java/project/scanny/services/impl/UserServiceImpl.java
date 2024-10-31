package project.scanny.services.impl;

import org.springframework.stereotype.Service;
import project.scanny.dao.UserRepository;
import project.scanny.models.User;
import project.scanny.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(@Valid @NotNull User user) {
        User savedUser = userRepository.save(user);
        log.info("Created user: {}", savedUser);
        return savedUser;
    }
}
