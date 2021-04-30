package prj.repository;

import prj.model.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * The LessonRepository interface is used for performing CRUD actions on the lessons table.
 * It extends JpaRepository which encapsulates the logic of these actions away.
 */
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    public Lesson findById(long id);

    /*
     * Finds the first lesson whose id is greater than the @param id
     */
    @Query(value = "SELECT * FROM lesson WHERE lesson.id > ?1 ORDER BY id ASC LIMIT 1", nativeQuery = true)
    public Lesson findLatestInComplete(long id);
}
