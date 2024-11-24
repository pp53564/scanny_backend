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

import java.util.Optional;

@RestController
@RequestMapping("/api/attempts")
public class UserQuestionAttemptController {
    private final UserQuestionAttemptService userQuestionAttemptService;
    private final UserService userService;
    private final QuestionService questionService;

    public UserQuestionAttemptController(UserQuestionAttemptService userQuestionAttemptService, UserService userService, QuestionService questionService) {
        this.userQuestionAttemptService = userQuestionAttemptService;
        this.userService = userService;
        this.questionService = questionService;
    }

    @PostMapping("/attempt")
    public ResponseEntity<?> recordAttempt(@ModelAttribute UserQuestionAttemptRequest userQuestionAttemptRequest) {
        UserQuestionAttempt userQuestionAttempt = UserQuestionAttemptMapper.toEntity(userQuestionAttemptRequest);
        System.out.println(userQuestionAttempt.getUser().getId());
        User user = userService.findById(userQuestionAttempt.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Question question = questionService.findById(userQuestionAttemptRequest.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        UserQuestionAttempt attempt = userQuestionAttemptService.findByUserAndQuestion(user, question);

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
                //String imagePath = imageService.saveImage(imageFile);
                attempt.setImagePath("imagePath");
            } else {
                return ResponseEntity.badRequest().body("Image is required when succeeded is true.");
            }
        }

        userQuestionAttemptService.save(attempt);

        return ResponseEntity.ok("Attempt recorded successfully.");
    }

}
