package prj.repository;

import prj.model.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    public Lesson findById(long id);
    @Query(value = "SELECT * FROM lesson WHERE lesson.id > ?1 ORDER BY id ASC LIMIT 1", nativeQuery = true)
    public Lesson findLatestInComplete(long id);
}
