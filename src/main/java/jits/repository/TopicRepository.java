package jits.repository;

import jits.model.Topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    public List<Topic> findAll();
    public Topic findById(long id);
}
