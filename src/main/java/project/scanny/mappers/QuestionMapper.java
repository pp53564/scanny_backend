package project.scanny.mappers;

import project.scanny.dto.QuestionDTO;
import project.scanny.models.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionDTO toDTO(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getSubject(),
                question.getCorrectImagePath()
        );
    }

    public static List<QuestionDTO> toDTOList(List<Question> questions) {
        return questions.stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
