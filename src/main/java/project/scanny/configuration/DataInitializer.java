package project.scanny.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.scanny.dao.UserRepository;
import project.scanny.models.User;
import project.scanny.models.enums.Role;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository   userRepo;
    private final PasswordEncoder  passwordEncoder;

    public DataInitializer(UserRepository userRepo,
                           PasswordEncoder passwordEncoder) {
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.findByUsername("teacher1").isEmpty()) {
            String hashed = passwordEncoder.encode("teacher1");
            userRepo.save(new User(
                    "teacher1",
                    hashed,
                    Role.TEACHER
            ));
        }
    }
}
