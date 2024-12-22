package project.scanny.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;

import java.util.List;

public interface UserQuestionAttemptRepository extends JpaRepository<UserQuestionAttempt, Long> {
    UserQuestionAttempt findByUserAndQuestion(User user, Question question);
    List<UserQuestionAttempt> findByUserIdAndQuestionIdIn(Long userId, List<Long> questionIds);
}
