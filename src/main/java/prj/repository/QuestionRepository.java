package prj.repository;

import prj.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * The QuestionRepository interface is used for performing CRUD actions on the questions table.
 * It extends JpaRepository which encapsulates the logic of these actions away.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {
    public Question findById(long id);
}
