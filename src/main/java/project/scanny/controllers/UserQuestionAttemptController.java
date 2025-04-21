package project.scanny.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.scanny.dto.AttemptResponse;
import project.scanny.models.User;
import project.scanny.requests.question.UserQuestionAttemptRequest;
import project.scanny.services.UserQuestionAttemptService;
import project.scanny.services.UserService;


@RestController
@RequestMapping("/api/attempts")
public class UserQuestionAttemptController {
    private final UserQuestionAttemptService userQuestionAttemptService;
    private final UserService userService;

    public UserQuestionAttemptController(UserQuestionAttemptService userQuestionAttemptService, UserService userService) {
        this.userQuestionAttemptService = userQuestionAttemptService;
        this.userService = userService;
    }

    @PostMapping("/attempt")
    public ResponseEntity<AttemptResponse> recordAttempt(
            @ModelAttribute UserQuestionAttemptRequest dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        try {
            AttemptResponse body = userQuestionAttemptService.processAttempt(user, dto);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
