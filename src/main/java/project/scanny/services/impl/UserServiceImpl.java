package project.scanny.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.scanny.dao.UserRepository;
import project.scanny.models.User;
import project.scanny.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(@Valid @NotNull User user) {
        User savedUser = userRepository.save(user);
        log.info("Created user: {}", savedUser);
        return savedUser;
    }
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createOrLoginUser(User user) {
        Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException("Invalid username or password");
            }
            return existingUser;
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User newUser = userRepository.save(user);
            log.info("Created new user: {}", newUser);
            return newUser;
        }
    }
}
