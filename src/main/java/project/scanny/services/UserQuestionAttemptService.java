package project.scanny.services;

import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;

public interface UserQuestionAttemptService {
    UserQuestionAttempt findByUserAndQuestion(User user, Question question);
    void save(UserQuestionAttempt attempt);

}
