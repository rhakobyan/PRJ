package prj.model;

import javax.persistence.*;

@Entity
@Table(name = "solution")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String solutionFile;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    private Problem problem;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSolutionFile() {
        return solutionFile;
    }

    public void setSolutionFile(String solutionFile) {
        this.solutionFile = solutionFile;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}