package project.scanny.services;

import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;

import java.util.List;

public interface UserQuestionAttemptService {
    UserQuestionAttempt findByUserAndQuestion(User user, Question question);
    List<UserQuestionAttempt> findByUserAndQuestionIds(Long userId, List<Long> questionIds);
    void save(UserQuestionAttempt attempt);
     List<UserQuestionAttempt> findByUserAndQuestionIdsAndLang(Long userId, List<Long> questionIds, String languageCode);
     UserQuestionAttempt findByUserAndQuestionAndLang(User User, Question question, String languageCode);
}
