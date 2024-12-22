package project.scanny.services;

import project.scanny.dto.QuestionDTO;
import project.scanny.dto.UserQuestionDTO;
import project.scanny.models.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<QuestionDTO> getQuestionsByLectureId(Long lectureId);
    List<UserQuestionDTO> getUserQuestionsByLectureId(Long lectureId, Long userId);
    Optional<Question> findById(Long questionId);
}
