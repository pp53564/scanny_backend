package project.scanny.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import project.scanny.requests.lecture.CreateLectureRequest;
import project.scanny.dto.LectureCreatedDto;
import project.scanny.dto.LectureDTO;
import project.scanny.dto.UserLectureDTO;
import project.scanny.models.User;
import project.scanny.services.LectureService;
import project.scanny.services.UserService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final UserService userService;

    @Autowired
    public LectureController(LectureService lectureService, UserService userService) {
        this.lectureService = lectureService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<LectureDTO>> getAllLectures() {
        try {
            List<LectureDTO> lectures = lectureService.getAllLectures();
            return ResponseEntity.ok(lectures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    @GetMapping("/user")
    public ResponseEntity<List<UserLectureDTO>> getAllUserLectures() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            List<UserLectureDTO> lectures = lectureService.getAllUserLectures(user.getId());
            return ResponseEntity.ok(lectures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/user/{selectedLangCode}")
    public ResponseEntity<List<UserLectureDTO>> getAllUserLangLectures(@PathVariable String selectedLangCode) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            List<UserLectureDTO> lectures = lectureService.getAllUserLanguageLectures(user.getId(), selectedLangCode);
            return ResponseEntity.ok(lectures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/teacher/createLecture")
    public ResponseEntity<LectureCreatedDto> createLecture(@RequestBody CreateLectureRequest createLectureRequest) {
//        System.out.println("createLectureRequest: " + createLectureRequest);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(new LectureCreatedDto("Successfully created a lecture"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User teacher = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            LectureCreatedDto dto = lectureService.createLecture(createLectureRequest, teacher.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode().value())
                    .body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
