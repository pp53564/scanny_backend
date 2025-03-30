package project.scanny.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;

import java.util.List;

public interface UserQuestionAttemptRepository extends JpaRepository<UserQuestionAttempt, Long> {
    UserQuestionAttempt findByUserAndQuestion(User user, Question question);
    UserQuestionAttempt findByUserAndQuestionAndLanguageCode(User user, Question question, String languageCode);
    List<UserQuestionAttempt> findByUserIdAndQuestionIdIn(Long userId, List<Long> questionIds);

    @Query("SELECT uqa FROM UserQuestionAttempt uqa " +
            "WHERE uqa.user.id = :userId " +
            "  AND uqa.question.id IN :questionIds " +
            "  AND uqa.languageCode = :langCode")
    List<UserQuestionAttempt> findByUserQuestionIdsAndLang(
            @Param("userId") Long userId,
            @Param("questionIds") List<Long> questionIds,
            @Param("langCode") String langCode
    );
}
