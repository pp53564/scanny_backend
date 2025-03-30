package project.scanny.dto;

public record UserQuestionLangDTO(Long id, String subject, String localizedSubject, Boolean succeeded, int attemptCount) {
}

