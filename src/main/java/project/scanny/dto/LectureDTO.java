package project.scanny.dto;

import java.util.List;

public record LectureDTO(Long id, String title, List<QuestionDTO> questions) {
}
