package project.scanny.mappers;

import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.requests.question.UserQuestionAttemptRequest;

public class UserQuestionAttemptMapper {
    public static UserQuestionAttempt toEntity(UserQuestionAttemptRequest request) {
        UserQuestionAttempt attempt = new UserQuestionAttempt();

        User user = new User();
        user.setId(request.userId());
        attempt.setUser(user);

        Question question = new Question();
        question.setId(request.questionId());
        attempt.setQuestion(question);

//        attempt.setSucceeded(request.succeeded());
        return attempt;
    }
}
