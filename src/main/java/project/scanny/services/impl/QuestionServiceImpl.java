package project.scanny.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.scanny.dao.QuestionRepository;
import project.scanny.dto.QuestionDTO;
import project.scanny.dto.UserQuestionDTO;
import project.scanny.mappers.QuestionMapper;
import project.scanny.models.Question;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.services.QuestionService;
import project.scanny.services.UserQuestionAttemptService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserQuestionAttemptService userQuestionAttemptService;

    public QuestionServiceImpl(QuestionRepository questionRepository, UserQuestionAttemptService userQuestionAttemptService) {
        this.questionRepository = questionRepository;
        this.userQuestionAttemptService = userQuestionAttemptService;
    }

    @Override
    public List<QuestionDTO> getQuestionsByLectureId(Long lectureId) {
        List<Question> questions = questionRepository.findByLectureId(lectureId);
        return QuestionMapper.toDTOList(questions);
    }

    @Override
    public List<UserQuestionDTO> getUserQuestionsByLectureId(Long lectureId, Long userId) {
        List<Question> questions = questionRepository.findByLectureId(lectureId);

        List<Long> questionIds = questions.stream().map(Question::getId).toList();
        List<UserQuestionAttempt> attempts = userQuestionAttemptService.findByUserAndQuestionIds(userId, questionIds);

        Map<Long, UserQuestionAttempt> attemptMap = attempts.stream()
                .collect(Collectors.toMap(attempt -> attempt.getQuestion().getId(), Function.identity()));

        return questions.stream()
                .map(question -> QuestionMapper.toUserQuestionDTO(question, attemptMap))
                .toList();
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return questionRepository.findById(questionId);
    }
}
