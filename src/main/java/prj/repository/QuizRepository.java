package prj.repository;

import prj.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    public Quiz findById(long id);

    @Query(value = "SELECT * FROM quiz WHERE quiz.id > ?1 ORDER BY id ASC LIMIT 1", nativeQuery = true)
    public Quiz findLatestInComplete(long id);
}
