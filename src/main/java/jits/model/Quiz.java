package jits.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @OneToMany(mappedBy = "quiz")
    private List<Question> questions;

    @Column(nullable = false)
    private double passPercent;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "quiz_complete",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> studentsCompleted;

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

    @Override
    public boolean equals(Object object) {
        if (object instanceof Quiz) {
            Quiz quiz = (Quiz) object;

            if (quiz.getId() == this.id)
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
