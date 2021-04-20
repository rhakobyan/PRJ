package prj.model;

import javax.persistence.*;
import prj.util.FileIO;
import java.io.IOException;
import java.util.List;

@Entity
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String problemFile;

    @Column(columnDefinition="tinyint(1) default 1")
    private boolean solutionRequired;

    @Transient
    private String problemBody;

    @Column(nullable = false)
    private int solutionStartIndex;
    @Column(nullable = false)
    private int solutionEndLength;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private Lesson lesson;

    @OneToMany(mappedBy = "problem")
    private List<Solution> solutions;

    @OneToMany(mappedBy = "problem")
    private List<Misconception> misconceptions;

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

    public boolean isSolutionRequired() {
        return solutionRequired;
    }

    public void setSolutionRequired(boolean solutionRequired) {
        this.solutionRequired = solutionRequired;
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

    public int getSolutionStartIndex() {
        return solutionStartIndex;
    }

    public void setSolutionStartIndex(int solutionStartIndex) {
        this.solutionStartIndex = solutionStartIndex;
    }

    public int getSolutionEndLength() {
        return solutionEndLength;
    }

    public void setSolutionEndLength(int solutionEndLength) {
        this.solutionEndLength = solutionEndLength;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    public List<Misconception> getMisconceptions() {
        return misconceptions;
    }

    public void setMisconceptions(List<Misconception> misconceptions) {
        this.misconceptions = misconceptions;
    }

    public void setProblemBody() {
        try {
            this.problemBody = FileIO.resourceFileToString(this.getProblemFile());
        } catch (IOException ex) {
            this.problemBody = "";
            ex.printStackTrace();
        }
    }
}
