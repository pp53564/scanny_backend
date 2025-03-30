package project.scanny.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.scanny.dto.QuestionDTO;
import project.scanny.dto.UserQuestionDTO;
import project.scanny.dto.UserQuestionLangDTO;
import project.scanny.models.User;
import project.scanny.services.QuestionService;
import project.scanny.services.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;

    @Autowired
    public QuestionController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByLecture(@PathVariable Long lectureId) {
        try {
            List<QuestionDTO> questions =  questionService.getQuestionsByLectureId(lectureId);
            return ResponseEntity.ok(questions);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/lecture/{lectureId}/user")
    public ResponseEntity<List<UserQuestionDTO>> getUserQuestionsByLecture(@PathVariable Long lectureId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            List<UserQuestionDTO> userQuestions = questionService.getUserQuestionsByLectureId(lectureId, user.getId());
            return ResponseEntity.ok(userQuestions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    @GetMapping("/lecture/{lectureId}/user/{langCode}")
    public ResponseEntity<List<UserQuestionLangDTO>> getUserQuestionsByLecture(@PathVariable Long lectureId, @PathVariable String langCode) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            List<UserQuestionLangDTO> userQuestions = questionService.getUserQuestionsByLectureIdAndLang(lectureId, user.getId(), langCode);
            return ResponseEntity.ok(userQuestions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
