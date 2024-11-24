package project.scanny.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "lectures")
@Data
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Question> questions;

    public Lecture() {}

    public Lecture(String title) {
        this.title = title;
    }
}
