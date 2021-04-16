package prj.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition="TEXT")
    private String explanation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @OneToOne(mappedBy = "lesson")
    private Problem problem;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "lesson_complete",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> studentsCompleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Set<User> getStudentsCompleted() {
        return studentsCompleted;
    }

    public void addStudentsCompleted(User user) {
        this.studentsCompleted.add(user);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Lesson) {
            Lesson lesson = (Lesson) object;

            if (lesson.getId() == this.id)
                return true;
        }

        return false;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 17;
        return prime * result + this.id.hashCode();
    }
}
