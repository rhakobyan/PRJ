package jits.model;

import org.springframework.core.io.ClassPathResource;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import jits.util.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Entity
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String problemFile;

    @Transient
    private String problemBody;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private Lesson lesson;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProblemFile() {
        return problemFile;
    }

    public void setProblemFile(String problemFile) {
        this.problemFile = problemFile;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getProblemBody() {
        return problemBody;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    @OneToMany(mappedBy = "problem")
    private List<Solution> solutions;

    public void setProblemBody() {
        try {
            this.problemBody = FileReader.resourceFileToString(this.getProblemFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
