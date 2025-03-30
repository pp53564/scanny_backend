package project.scanny.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "user_question_attempts")
@ToString(exclude = {"user"})
@Data
public class UserQuestionAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private int attemptCount = 0;

    @Column(nullable = false)
    private boolean succeeded = false;

    @Column(nullable = false)
    private String languageCode;

    @Column(name = "image_path")
    private String imagePath;

    public UserQuestionAttempt(User user, Question question) {
        this.user = user;
        this.question = question;
        this.attemptCount = 0;
        this.succeeded = false;
    }
    public UserQuestionAttempt() {
    }

    public UserQuestionAttempt(User user, Question question, String languageCode) {
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }
}
