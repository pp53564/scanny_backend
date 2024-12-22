package project.scanny.mappers;

import project.scanny.dto.QuestionDTO;
import project.scanny.dto.UserQuestionDTO;
import project.scanny.models.Question;
import project.scanny.models.UserQuestionAttempt;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionDTO toDTO(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getSubject()
        );
    }

    public static List<QuestionDTO> toDTOList(List<Question> questions) {
        return questions.stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static UserQuestionDTO toUserQuestionDTO(Question question, Map<Long, UserQuestionAttempt> attemptMap) {
        UserQuestionAttempt attempt = attemptMap.get(question.getId());
        boolean succeeded = Optional.ofNullable(attempt).map(UserQuestionAttempt::isSucceeded).orElse(false);
        int attemptCount = Optional.ofNullable(attempt).map(UserQuestionAttempt::getAttemptCount).orElse(0);

        return new UserQuestionDTO(question.getId(), question.getSubject(), succeeded, attemptCount);
    }

}
