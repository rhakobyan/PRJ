package jits.repository;

import jits.model.Lesson;

import jits.model.Topic;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LessonRepository extends JpaRepository<Lesson, Long> {
    public Lesson findById(long id);
}
