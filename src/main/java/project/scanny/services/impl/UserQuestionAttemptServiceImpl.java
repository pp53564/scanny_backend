package project.scanny.services.impl;

import com.google.cloud.vision.v1.EntityAnnotation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.scanny.dao.QuestionRepository;
import project.scanny.dao.UserQuestionAttemptRepository;
import project.scanny.dto.AttemptResponse;
import project.scanny.mappers.UserQuestionAttemptMapper;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.requests.question.UserQuestionAttemptRequest;
import project.scanny.services.UserQuestionAttemptService;
import project.scanny.services.VisionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class UserQuestionAttemptServiceImpl implements UserQuestionAttemptService {
    private final UserQuestionAttemptRepository userQuestionAttemptRepository;
    private final VisionService visionService;
    private final QuestionRepository questionRepository;
    private final Path uploadDir = Paths.get("src/main/java/project/scanny/images");

    public UserQuestionAttemptServiceImpl(UserQuestionAttemptRepository userQuestionAttemptRepository, VisionService visionService, QuestionRepository questionRepository) {
        this.userQuestionAttemptRepository = userQuestionAttemptRepository;
        this.visionService = visionService;
        this.questionRepository = questionRepository;
    }

    public UserQuestionAttempt findByUserAndQuestion(User user, Question question) {
        return userQuestionAttemptRepository.findByUserAndQuestion(user, question);
    }

    @Override
    public List<UserQuestionAttempt> findByUserAndQuestionIds(Long userId, List<Long> questionIds) {
        return userQuestionAttemptRepository.findByUserIdAndQuestionIdIn(userId, questionIds);
    }

    public void save(UserQuestionAttempt attempt) {
        userQuestionAttemptRepository.save(attempt);
    }

    public List<UserQuestionAttempt> findByUserAndQuestionIdsAndLang(
            Long userId,
            List<Long> questionIds,
            String languageCode
    ) {
        return userQuestionAttemptRepository.findByUserQuestionIdsAndLang(userId, questionIds, languageCode);
    }

    @Override
    public UserQuestionAttempt findByUserAndQuestionAndLang(User User, Question question, String languageCode) {
        return userQuestionAttemptRepository.findByUserAndQuestionAndLanguageCode(User, question, languageCode);
    }

    @Override
    public AttemptResponse processAttempt(User user, UserQuestionAttemptRequest userQuestionAttemptRequest) throws Exception {
        Question question = questionRepository.findById(userQuestionAttemptRequest.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        UserQuestionAttempt userQuestionAttempt = UserQuestionAttemptMapper.toEntity(userQuestionAttemptRequest);

        String languageCode = userQuestionAttempt.getLanguageCode();

        UserQuestionAttempt attempt =
                userQuestionAttemptRepository.findByUserAndQuestionAndLanguageCode(user, question, languageCode);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        if (attempt == null) {
            attempt = new UserQuestionAttempt();
            attempt.setUser(user);
            attempt.setQuestion(question);
            attempt.setAttemptCount(1);
            attempt.setLanguageCode(languageCode);
        } else {
            attempt.incrementAttemptCount();
        }

        MultipartFile imageFile = userQuestionAttemptRequest.correctImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            List<EntityAnnotation> labels;
            labels = visionService.detectLabels(userQuestionAttemptRequest.correctImage());

            String correct = question.getBaseSubject().toLowerCase();
            boolean isCorrect = false;
            float confidenceScore = 0;

            for(EntityAnnotation label : labels) {
                if(label.getDescription().equalsIgnoreCase(correct)) {
                    isCorrect = true;
                    confidenceScore = label.getScore();
                    break;
                }
            }
            if (isCorrect) {
                attempt.setSucceeded(true);
                if (!imageFile.isEmpty()) {
                    String imagePath = saveImageLocally(imageFile);
                    attempt.setImagePath(imagePath);
                }
            }

            userQuestionAttemptRepository.save(attempt);

            String message;
            if (isCorrect) {
                message = "Correct answer!";
            } else if (imageFile.isEmpty()) {
                message = "No image provided.";
            } else {
                message = "Incorrect answer. Try again.";
            }

            return new AttemptResponse(
                    isCorrect,
                    isCorrect ? confidenceScore : labels.getFirst().getScore(),
                    message,
                    isCorrect ? correct : labels.getFirst().getDescription()
            );
        } else {
            userQuestionAttemptRepository.save(attempt);
            return new AttemptResponse(
                    false,
                    0,
                    "No image.",
                    ""
            );
        }
    }

    private String saveImageLocally(MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + fileExtension;
        Path filePath = uploadDir.resolve(filename);

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }
}
