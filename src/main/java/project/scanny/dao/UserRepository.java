package project.scanny.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import project.scanny.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
