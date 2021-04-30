package prj.repository;

import prj.model.Topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * The TopicRepository interface is used for performing CRUD actions on the topics table.
 * It extends JpaRepository which encapsulates the logic of these actions away.
 */
public interface TopicRepository extends JpaRepository<Topic, Long> {
    public List<Topic> findAll();
    public Topic findById(long id);
}
