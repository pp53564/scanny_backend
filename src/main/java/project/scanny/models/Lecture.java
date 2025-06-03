package project.scanny.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "lectures", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Data
@ToString(exclude = {"questions"})
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

//    @Column(nullable = false)
//    private String languageCode;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Question> questions;

    public Lecture() {}

    public Lecture(String title) {
        this.title = title;
    }
}
