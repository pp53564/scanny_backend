package project.scanny.dto;

public record AttemptDTO(
        Long id,
        Long userId,
        Long questionId,
        int attemptCount,
        boolean succeeded,
        String imagePath
) {
}

