package project.scanny.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.scanny.dao.LectureRepository;
import project.scanny.dto.LectureDTO;
import project.scanny.dto.UserLectureDTO;
import project.scanny.mappers.LectureMapper;
import project.scanny.models.Question;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.services.LectureService;
import project.scanny.services.UserQuestionAttemptService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LectureServiceImpl implements LectureService {
    private final LectureRepository lectureRepository;
    private final UserQuestionAttemptService userQuestionAttemptService;

    public LectureServiceImpl(LectureRepository lectureRepository, UserQuestionAttemptService userQuestionAttemptService) {
        this.lectureRepository = lectureRepository;
        this.userQuestionAttemptService = userQuestionAttemptService;
    }

    public List<LectureDTO> getAllLectures() {
        return lectureRepository.findAll().stream()
                .map(LectureMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserLectureDTO> getAllUserLectures(Long userId) {
        return lectureRepository.findAll().stream()
                .map(lecture -> {
                    // For each lecture, determine if all questions are answered
                    boolean allQuestionsSucceeded = areAllQuestionsAnswered(lecture.getId(), userId);
                    return new UserLectureDTO(
                            lecture.getId(),
                            lecture.getTitle(),
                            allQuestionsSucceeded
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserLectureDTO> getAllUserLanguageLectures(Long userId, String selectedLangCode) {
        return lectureRepository.findAll().stream()
                .filter(lecture -> lecture.getLanguageCode().equals(selectedLangCode))
                .map(lecture -> {
                    boolean allQuestionsSucceeded = areAllQuestionsAnswered(lecture.getId(), userId);
                    return new UserLectureDTO(
                            lecture.getId(),
                            lecture.getTitle(),
                            allQuestionsSucceeded
                    );
                })
                .collect(Collectors.toList());
    }

    private boolean areAllQuestionsAnswered(Long lectureId, Long userId) {
        var lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        List<Long> questionIds = lecture.getQuestions().stream().map(Question::getId).toList();

        var userAttempts = userQuestionAttemptService.findByUserAndQuestionIds(userId, questionIds);

        var answeredQuestionIds = userAttempts.stream()
                .filter(UserQuestionAttempt::isSucceeded)
                .map(attempt -> attempt.getQuestion().getId())
                .collect(Collectors.toSet());

        return answeredQuestionIds.containsAll(questionIds);
    }
}

