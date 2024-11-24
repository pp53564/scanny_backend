package project.scanny.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.scanny.dao.QuestionRepository;
import project.scanny.dto.QuestionDTO;
import project.scanny.mappers.QuestionMapper;
import project.scanny.models.Question;
import project.scanny.services.QuestionService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

//    public List<Question> getQuestionsByLectureId(Long lectureId) {
//        return questionRepository.findByLectureId(lectureId);
//    }
    public List<QuestionDTO> getQuestionsByLectureId(Long lectureId) {
        List<Question> questions = questionRepository.findByLectureId(lectureId);
        return QuestionMapper.toDTOList(questions);
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return questionRepository.findById(questionId);
    }
}
