package project.scanny.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import project.scanny.dto.AttemptResponse;
import project.scanny.exceptions.AlreadyAnsweredException;
import project.scanny.exceptions.EmptyImageException;
import project.scanny.exceptions.ImageStorageException;
import project.scanny.requests.question.UserQuestionAttemptRequest;
import project.scanny.services.QuestionService;
import project.scanny.services.UserQuestionAttemptService;
import project.scanny.services.UserService;
import project.scanny.services.VisionService;
import java.io.IOException;


@RestController
@RequestMapping("/api/attempts")
public class UserQuestionAttemptController {
    private final UserQuestionAttemptService userQuestionAttemptService;

    public UserQuestionAttemptController(UserQuestionAttemptService userQuestionAttemptService) {
        this.userQuestionAttemptService = userQuestionAttemptService;
    }

    @PostMapping("/attempt")
    public ResponseEntity<AttemptResponse> recordAttempt(
            @ModelAttribute UserQuestionAttemptRequest dto,
            Authentication auth) throws IOException {

        try {
            AttemptResponse body = userQuestionAttemptService.processAttempt(auth.getName(), dto);
            return ResponseEntity.ok(body);

        } catch (EmptyImageException e) {
            return ResponseEntity.badRequest()
                    .body(new AttemptResponse(false, 0, e.getMessage(), ""));

        } catch (AlreadyAnsweredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AttemptResponse(true, 0, e.getMessage(), ""));

        } catch (ImageStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AttemptResponse(false, 0, e.getMessage(), ""));

        }
    }
}
