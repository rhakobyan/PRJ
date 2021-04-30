package prj.model;

import javax.persistence.*;
import java.util.List;

/*
 * The Topic class represents a topic entity in the system.
 * Spring Data JPA annotations are applied to the class in order to represent it as a table inside the database.
 * Using Spring Data JPA its fields are marked as columns in the table.
 */
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    // URL link to an image associated with it
    @Column(nullable = false)
    private String image;

    // The list of lessons that are inside of the topic
    @OneToMany(mappedBy = "topic")
    private List<Lesson> lessons;

    // The quiz that is inside of the topic
    @OneToOne(mappedBy = "topic")
    private Quiz quiz;

    /*
     * Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
