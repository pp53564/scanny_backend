package project.scanny.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.scanny.dao.QuestionRepository;
import project.scanny.dto.AnsweredQuestionDTO;
import project.scanny.dto.QuestionDTO;
import project.scanny.dto.UserQuestionDTO;
import project.scanny.dto.UserQuestionLangDTO;
import project.scanny.mappers.QuestionMapper;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.services.QuestionService;
import project.scanny.services.UserQuestionAttemptService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserQuestionAttemptService userQuestionAttemptService;
    private final WordTranslationService wordTranslationService;

    public QuestionServiceImpl(QuestionRepository questionRepository, UserQuestionAttemptService userQuestionAttemptService, WordTranslationService wordTranslationService) {
        this.questionRepository = questionRepository;
        this.userQuestionAttemptService = userQuestionAttemptService;
        this.wordTranslationService = wordTranslationService;
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

    public List<UserQuestionLangDTO> getUserQuestionsByLectureIdAndLang(Long lectureId, Long userId, String langCode) {
        List<Question> questions = questionRepository.findByLectureId(lectureId);
        List<Long> questionIds = questions.stream().map(Question::getId).toList();

//        Collections.shuffle(questions);

        List<UserQuestionAttempt> attempts = userQuestionAttemptService
                .findByUserAndQuestionIdsAndLang(userId, questionIds, langCode);

        Map<Long, List<UserQuestionAttempt>> attemptsByQuestion = attempts.stream()
                .collect(Collectors.groupingBy(
                        uqa -> uqa.getQuestion().getId()
                ));


        List<UserQuestionLangDTO> list = new java.util.ArrayList<>(questions.stream().map(question -> {
            String baseWord = question.getBaseSubject();
            String localizedWord = wordTranslationService.getTranslation(baseWord, langCode);

            List<UserQuestionAttempt> questionAttempts = attemptsByQuestion.getOrDefault(question.getId(), List.of());

            int attemptCount = questionAttempts.isEmpty() ? 0 : questionAttempts.getFirst().getAttemptCount();

            boolean succeeded = !questionAttempts.isEmpty() && questionAttempts.getFirst().isSucceeded();

            return new UserQuestionLangDTO(
                    question.getId(),
                    baseWord,
                    localizedWord,
                    succeeded,
                    attemptCount
            );
        }).toList());
        Collections.shuffle(list);
        list.sort(Comparator.comparing(UserQuestionLangDTO::succeeded));
        return list;
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    @Override
    public AnsweredQuestionDTO findSuccessfulAttempt(User user, Long questionId, String langCode) {
        Question question = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);
        UserQuestionAttempt attempt = userQuestionAttemptService.findByUserAndQuestionAndLang(user, question, langCode);

        String questionText = wordTranslationService.getTranslation(attempt.getQuestion().getBaseSubject(),langCode);
        Path imgPath = Paths.get(attempt.getImagePath());
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(imgPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new AnsweredQuestionDTO(questionText, bytes);
    }

}
