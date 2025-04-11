package com.jo.api.security.repository;

import com.jo.api.security.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value ="SELECT u.* FROM users u" +
            "JOIN user_roles ur ON ur.user_id = u.id" +
            "JOIN roles r ON r.id = ur.role_id" +
            "WHERE r.name = ?1",
            nativeQuery = true)
    List<User> findUserByRoles(String role);

    boolean existsByUsername(@NotBlank @Size(min = 3, max = 50) @Email String username);
}
