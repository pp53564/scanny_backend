package project.scanny.controllers;

import com.google.cloud.vision.v1.EntityAnnotation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.scanny.dto.AttemptResponse;
import project.scanny.mappers.UserQuestionAttemptMapper;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.requests.question.UserQuestionAttemptRequest;
import project.scanny.services.QuestionService;
import project.scanny.services.UserQuestionAttemptService;
import project.scanny.services.UserService;
import project.scanny.services.VisionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/attempts")
public class UserQuestionAttemptController {
    private final UserQuestionAttemptService userQuestionAttemptService;
    private final VisionService visionService;
    private final UserService userService;
    private final QuestionService questionService;
    private final Path uploadDir = Paths.get("src/main/java/project/scanny/images");

    public UserQuestionAttemptController(UserQuestionAttemptService userQuestionAttemptService, VisionService visionService, UserService userService, QuestionService questionService) {
        this.userQuestionAttemptService = userQuestionAttemptService;
        this.visionService = visionService;
        this.userService = userService;
        this.questionService = questionService;
    }

    @PostMapping("/attempt")
    public AttemptResponse recordAttempt(@ModelAttribute UserQuestionAttemptRequest userQuestionAttemptRequest) throws IOException {
        UserQuestionAttempt userQuestionAttempt = UserQuestionAttemptMapper.toEntity(userQuestionAttemptRequest);

//        User user = userService.findById(userQuestionAttempt.getUser().getId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Question question = questionService.findById(userQuestionAttemptRequest.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        String languageCode = userQuestionAttempt.getLanguageCode();

        UserQuestionAttempt attempt = userQuestionAttemptService.findByUserAndQuestionAndLang(user, question, languageCode);

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
            if (attempt.isSucceeded()) {
                return ResponseEntity.badRequest().body(new AttemptResponse(
                        true, 0, "Question already answered correctly.", ""
                )).getBody();
            }
            attempt.incrementAttemptCount();
        }
        if(userQuestionAttemptRequest.correctImage() != null) {
            List<EntityAnnotation> labels = List.of();
//            labels = visionService.detectLabels(userQuestionAttemptRequest.correctImage());

            String correct = question.getBaseSubject().toLowerCase();
            boolean isCorrect = false;
            float confidenceScore = 0;

            for(EntityAnnotation entityAnnotation : labels) {
                if(entityAnnotation.getDescription().equalsIgnoreCase(correct)) {
                    isCorrect = true;
                    confidenceScore = entityAnnotation.getScore();
                    break;
                }
            }
            if (isCorrect) {
                attempt.setSucceeded(true);

                MultipartFile imageFile = userQuestionAttemptRequest.correctImage();
                if (!imageFile.isEmpty()) {
                    try {
                        String imagePath = saveImageLocally(imageFile);
                        attempt.setImagePath(imagePath);
                    } catch (IOException e) {
                        return ResponseEntity.status(500).body(new AttemptResponse(
                                false, 0, "Failed to save image: " + e.getMessage(), ""
                        )).getBody();
                    }
                } else {
                    return ResponseEntity.badRequest().body(new AttemptResponse(
                            false, 0, "Image is required when succeeded is true.", ""
                    )).getBody();
                }
            }
            userQuestionAttemptService.save(attempt);
//            return ResponseEntity.ok(new AttemptResponse(
//                    isCorrect,
//                    isCorrect ? confidenceScore : labels.getFirst().getScore(),
//                    isCorrect ? "Correct answer!" : "Incorrect answer. Try again.",
//                    isCorrect ? correct : labels.getFirst().getDescription()
//            )).getBody();
            return ResponseEntity.ok(new AttemptResponse(
                    isCorrect,
                   0,
                   "Incorrect answer. Try again.", ""
            )).getBody();
        } else {
            userQuestionAttemptService.save(attempt);
            return ResponseEntity.ok(new AttemptResponse(
                    false,
                    0,
                     "No image.",
                    ""
            )).getBody();
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
