package prj.model;

import prj.dto.UserDto;

import javax.persistence.*;
import java.util.Set;

/*
 * The User class represents a user entity in the system.
 * Spring Data JPA annotations are applied to the class in order to represent it as a table inside the database.
 * Using Spring Data JPA its fields are marked as columns in the table.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    // The set of lessons that the user has completed.
    @ManyToMany(mappedBy = "studentsCompleted", fetch = FetchType.EAGER)
    Set<Lesson> completedLessons;

    // The set of quizzes that the user has completed.
    @ManyToMany(mappedBy = "studentsCompleted", fetch = FetchType.EAGER)
    Set<Quiz> completedQuizzes;

    /*
     * Constructor.
     * Constructs a user from a UserDto object.
     */
    public User(UserDto userDto) {
        this.email = userDto.getEmail();
        this.password = userDto.getPassword();
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
    }

    /*
     * The default constructor.
     * Initialises an empty user.
     */
    public User() {}

    /*
     * Getters and Setters.
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Lesson> getCompletedLessons() {
        return completedLessons;
    }

    public void addCompletedLesson(Lesson lesson) {
        this.completedLessons.add(lesson);
    }

    public void setCompletedLessons(Set<Lesson> completedLessons) {
        this.completedLessons = completedLessons;
    }

    public Set<Quiz> getCompletedQuizzes() {
        return completedQuizzes;
    }

    public void addCompletedQuiz(Quiz completedQuiz) {
        this.completedQuizzes.add(completedQuiz);
    }

    public void setCompletedQuizzes(Set<Quiz> completedQuizzes) {
        this.completedQuizzes = completedQuizzes;
    }
}
