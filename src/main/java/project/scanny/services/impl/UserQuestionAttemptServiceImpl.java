package project.scanny.services.impl;

import org.springframework.stereotype.Service;
import project.scanny.dao.UserQuestionAttemptRepository;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.services.UserQuestionAttemptService;

@Service
public class UserQuestionAttemptServiceImpl implements UserQuestionAttemptService {
    private final UserQuestionAttemptRepository userQuestionAttemptRepository;

    public UserQuestionAttemptServiceImpl(UserQuestionAttemptRepository userQuestionAttemptRepository) {
        this.userQuestionAttemptRepository = userQuestionAttemptRepository;
    }

    public UserQuestionAttempt findByUserAndQuestion(User user, Question question) {
        return userQuestionAttemptRepository.findByUserAndQuestion(user, question);
    }

    public void save(UserQuestionAttempt attempt) {
        userQuestionAttemptRepository.save(attempt);
    }
}
