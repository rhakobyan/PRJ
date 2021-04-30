package prj.model;

import javax.persistence.*;
import java.util.Set;

/*
 * The Lesson class represents a lesson entity in the system.
 * Spring Data JPA annotations are applied to the class in order to represent it as a table inside the database.
 * Using Spring Data JPA its fields are marked as columns in the table.
 */
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

    // The topic in which this lesson is.
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    // The problem component of the lesson represented as a separate class.
    @OneToOne(mappedBy = "lesson")
    private Problem problem;

    // The list of students that have completed this lesson.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "lesson_complete",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> studentsCompleted;

    /*
     * Getters and setters.
     */

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

    public void setStudentsCompleted(Set<User> studentsCompleted) {
        this.studentsCompleted = studentsCompleted;
    }

    public void addStudentsCompleted(User user) {
        this.studentsCompleted.add(user);
    }

    /*
     * Overriding the default equals method of the class.
     * Two lessons are said to be equal if they have the same ID.
     * @param object the Object that is the current one is being compared to.
     * @return whether the current object equals @param.
     */
    @Override
    public boolean equals(Object object) {
        // If the object is not a lesson, then it is not equal to the current lesson by default
        if (object instanceof Lesson) {
            Lesson lesson = (Lesson) object;

            // Check ids
            if (lesson.getId() == this.id)
                return true;
        }

        return false;
    }

    /*
     * Overriding the default hashCode method of the class.
     * @return the hash code of the current object.
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 17;
        return prime * result + this.id.hashCode();
    }
}
