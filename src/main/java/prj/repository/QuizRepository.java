package prj.repository;

import prj.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * The QuestionRepository interface is used for performing CRUD actions on the quizzes table.
 * It extends JpaRepository which encapsulates the logic of these actions away.
 */
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    public Quiz findById(long id);

    /*
     * Finds the first quiz whose id is greater than the @param id
     */
    @Query(value = "SELECT * FROM quiz WHERE quiz.id > ?1 ORDER BY id ASC LIMIT 1", nativeQuery = true)
    public Quiz findLatestInComplete(long id);
}
