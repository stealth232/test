package ru.clevertec.check.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.check.model.user.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
