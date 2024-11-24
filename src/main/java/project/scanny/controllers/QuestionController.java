package project.scanny.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.scanny.dto.QuestionDTO;
import project.scanny.models.Question;
import project.scanny.services.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
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
}
