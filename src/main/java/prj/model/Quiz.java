package prj.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/*
 * The Quiz class represents a quiz entity in the system.
 * Spring Data JPA annotations are applied to the class in order to represent it as a table inside the database.
 * Using Spring Data JPA its fields are marked as columns in the table.
 */
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    // The topic that this quiz is inside of.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    // The list of questions that are part of this quiz.
    @OneToMany(mappedBy = "quiz")
    private List<Question> questions;

    // The percentage that is necessary in order to pass the quiz.
    @Column(nullable = false)
    private double passPercent;

    // The list of students that have completed this quiz.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "quiz_complete",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> studentsCompleted;

    /*
     * Getters and setters
     */

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public double getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(double passPercent) {
        this.passPercent = passPercent;
    }

    public Set<User> getStudentsCompleted() {
        return studentsCompleted;
    }

    public void addStudentsCompleted(User user) {
        this.studentsCompleted.add(user);
    }

    public void setStudentsCompleted(Set<User> studentsCompleted) {
        this.studentsCompleted = studentsCompleted;
    }

    /*
     * Overriding the default equals method of the class.
     * Two quizzes are said to be equal if they have the same ID.
     * @param object the Object that is the current one is being compared to.
     * @return whether the current object equals @param.
     */
    @Override
    public boolean equals(Object object) {
        // If the object is not a lesson, then it is not equal to the current lesson by default
        if (object instanceof Quiz) {
            Quiz quiz = (Quiz) object;

            // Check ids
            if (quiz.getId() == this.id)
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
