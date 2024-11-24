package project.scanny.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "questions")
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(name = "correct_image_path", nullable = false)
    private String correctImagePath;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    @JsonBackReference
    private Lecture lecture;

    // One Question can have many UserQuestionAttempts
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<UserQuestionAttempt> attempts;

    public Question() {}

    public Question(String subject, String correctImagePath, Lecture lecture) {
        this.subject = subject;
        this.correctImagePath = correctImagePath;
        this.lecture = lecture;
    }
}
