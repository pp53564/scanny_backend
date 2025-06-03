package project.scanny.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import project.scanny.models.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    boolean existsByTitleIgnoreCase(String title);
}
