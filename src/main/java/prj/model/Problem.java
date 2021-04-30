package prj.model;

import javax.persistence.*;
import prj.util.FileIO;
import java.io.IOException;
import java.util.List;

/*
 * The Problem class represents a problem entity in the system.
 * Spring Data JPA annotations are applied to the class in order to represent it as a table inside the database.
 * Using Spring Data JPA its fields are marked as columns in the table.
 */
@Entity
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String problemFile;

    // whether a solution is required in order to to pass this problem.
    // True by default.
    @Column(columnDefinition="tinyint(1) default 1")
    private boolean solutionRequired;

    // String representation of the problem file
    @Transient
    private String problemBody;

    // The index at which the relevant code logic is written.
    // This lets ignore the irrelevant beginning parts of the code that are always the same.
    @Column(nullable = false)
    private int solutionStartIndex;

    // The index at which the relevant code logic ends.
    // This lets ignore the irrelevant ending parts of the code that are always the same.
    @Column(nullable = false)
    private int solutionEndLength;

    // The lesson that this problem is part of.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private Lesson lesson;

    // The list of model solutions for this problem.
    @OneToMany(mappedBy = "problem")
    private List<Solution> solutions;

    // The list of misconceptions for this problem.
    @OneToMany(mappedBy = "problem")
    private List<Misconception> misconceptions;

    /*
     * Getters and setters
     */

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

    /*
     * Sets the problemBody field of the object to the contents of the problem file.
     */
    public void setProblemBody() {
        try {
            this.problemBody = FileIO.resourceFileToString(this.getProblemFile());
        } catch (IOException ex) {
            this.problemBody = "";
            ex.printStackTrace();
        }
    }
}
