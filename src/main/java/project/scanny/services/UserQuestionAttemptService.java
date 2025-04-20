package project.scanny.services;

import project.scanny.dto.AttemptResponse;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.requests.question.UserQuestionAttemptRequest;

import java.io.IOException;
import java.util.List;

public interface UserQuestionAttemptService {
    UserQuestionAttempt findByUserAndQuestion(User user, Question question);
    List<UserQuestionAttempt> findByUserAndQuestionIds(Long userId, List<Long> questionIds);
    void save(UserQuestionAttempt attempt);
     List<UserQuestionAttempt> findByUserAndQuestionIdsAndLang(Long userId, List<Long> questionIds, String languageCode);
     UserQuestionAttempt findByUserAndQuestionAndLang(User User, Question question, String languageCode);
    AttemptResponse processAttempt(String name, UserQuestionAttemptRequest userQuestionAttemptRequest) throws IOException;
}
