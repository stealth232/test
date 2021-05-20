package ru.clevertec.check.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import ru.clevertec.check.model.user.User;

import java.util.List;
import java.util.Optional;

@RepositoryRestController
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByLogin(String login);

    Optional<User> findUserByLogin(String login);

    Integer deleteUserById(Integer login);

    User getUserById(Integer id);

    boolean existsById(Integer id);

    Optional<User> getUserByLoginAndPassword(String login, String password);

    @Query(value = "select a.id, a.login, c.name from users_security a " +
            "inner join users_security_roles b on a.id = b.user_id " +
            "inner join roles c on b.roles_id = c.id", nativeQuery = true)
    List<Object[]> getUserByRoles();
}