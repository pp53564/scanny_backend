package project.scanny.dto;

public record UserQuestionDTO(Long id, String subject, String localizedSubject, Boolean succeeded, int attemptCount) {
}
