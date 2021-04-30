package prj.repository;

import prj.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * The UserRepository interface is used for performing CRUD actions on the users table.
 * It extends JpaRepository which encapsulates the logic of these actions away.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}
