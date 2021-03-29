package prj.model;

import javax.persistence.*;

@Entity
@Table(name = "misconception")
public class Misconception {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String hint;

    @Column(nullable = false, columnDefinition="TEXT")
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    private Problem problem;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
