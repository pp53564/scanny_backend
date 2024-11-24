package project.scanny.requests.question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UserQuestionAttemptRequest(
        @NotNull Long userId,
        @NotNull Long questionId,
        MultipartFile correctImage,
        boolean succeeded
) {
}