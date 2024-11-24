package project.scanny.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import project.scanny.models.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByLectureId(Long lectureId);
}
