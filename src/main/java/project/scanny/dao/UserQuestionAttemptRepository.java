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
    List<UserQuestionAttempt> findAllByLanguageCode(String languageCode);

    @Query("SELECT uqa FROM UserQuestionAttempt uqa " +
            "WHERE uqa.user.id = :userId " +
            "  AND uqa.question.id IN :questionIds " +
            "  AND uqa.languageCode = :langCode")
    List<UserQuestionAttempt> findByUserQuestionIdsAndLang(
            @Param("userId") Long userId,
            @Param("questionIds") List<Long> questionIds,
            @Param("langCode") String langCode
    );

//    UserQuestionAttempt findByUserIdAndQuestionIdAndLanguageCodeAndSucceededTrue(Long id, Long questionId, String languageCode);

//    @Query("""
//    SELECT NEW project.scanny.dto.StatsPerUserAndLanguageDTO(
//        ua.languageCode,
//        COUNT(DISTINCT q.id),
//        COUNT(DISTINCT ua.question.id),
//        COUNT(CASE WHEN ua.succeeded = true THEN 1 ELSE null END),
//        COUNT(CASE WHEN ua.succeeded = true THEN 1 ELSE null END),
//        COUNT(ua.id),
//        AVG(ua.attemptCount * 1.0),
//        AVG(CASE WHEN ua.succeeded = true THEN ua.attemptCount * 1.0 ELSE null END)
//    )
//    FROM UserQuestionAttempt ua
//    JOIN ua.question q
//    WHERE ua.user.id = :userId
//    GROUP BY ua.languageCode
//""")
//    List<StatsPerUserAndLanguageDTO> getStatsPerUserAndLanguages(@Param("userId") Long userId);

}
