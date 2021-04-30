package prj.model;

import javax.persistence.*;

/*
 * The Solution class represents a problem solution entity in the system.
 * Spring Data JPA annotations are applied to the class in order to represent it as a table inside the database.
 * Using Spring Data JPA its fields are marked as columns in the table.
 */
@Entity
@Table(name = "solution")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String solutionFile;

    // The problem that this object is a solution of.
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    private Problem problem;

    /*
     * Getters and setters
     */

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