package project.scanny.dto;

public record UserQuestionDTO(Long id, String subject, Boolean succeeded, int attemptCount) {
}
