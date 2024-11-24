package project.scanny.services;

import project.scanny.dto.QuestionDTO;
import project.scanny.models.Question;
import project.scanny.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<QuestionDTO> getQuestionsByLectureId(Long lectureId);
    Optional<Question> findById(Long questionId);
}
