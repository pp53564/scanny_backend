package project.scanny.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.scanny.mappers.UserQuestionAttemptMapper;
import project.scanny.models.Question;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.requests.question.UserQuestionAttemptRequest;
import project.scanny.services.QuestionService;
import project.scanny.services.UserQuestionAttemptService;
import project.scanny.services.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@RestController
@RequestMapping("/api/attempts")
public class UserQuestionAttemptController {
    private final UserQuestionAttemptService userQuestionAttemptService;
    private final UserService userService;
    private final QuestionService questionService;
    private final Path uploadDir = Paths.get("src/main/java/project/scanny/images");

    public UserQuestionAttemptController(UserQuestionAttemptService userQuestionAttemptService, UserService userService, QuestionService questionService) {
        this.userQuestionAttemptService = userQuestionAttemptService;
        this.userService = userService;
        this.questionService = questionService;
    }

    @PostMapping("/attempt")
    public ResponseEntity<String> recordAttempt(@ModelAttribute UserQuestionAttemptRequest userQuestionAttemptRequest) throws IOException {
        UserQuestionAttempt userQuestionAttempt = UserQuestionAttemptMapper.toEntity(userQuestionAttemptRequest);
        System.out.println(userQuestionAttempt.getUser().getId());
        User user = userService.findById(userQuestionAttempt.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Question question = questionService.findById(userQuestionAttemptRequest.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        UserQuestionAttempt attempt = userQuestionAttemptService.findByUserAndQuestion(user, question);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        if (attempt == null) {
            attempt = new UserQuestionAttempt(user, question);
            attempt.setAttemptCount(1);
        } else {
            if (attempt.isSucceeded()) {
                return ResponseEntity.badRequest().body("Question already answered correctly.");
            }
            attempt.incrementAttemptCount();
        }

        if (userQuestionAttemptRequest.succeeded()) {
            attempt.setSucceeded(true);

            MultipartFile imageFile = userQuestionAttemptRequest.correctImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imagePath = saveImageLocally(imageFile);
                    attempt.setImagePath(imagePath);
                } catch (IOException e) {
                    return ResponseEntity.status(500).body("Failed to save image: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Image is required when succeeded is true.");
            }
        }
        userQuestionAttemptService.save(attempt);
        return ResponseEntity.ok("Attempt recorded successfully.");
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
