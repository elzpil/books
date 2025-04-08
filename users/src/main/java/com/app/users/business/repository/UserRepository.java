package com.app.users.business.repository;

import com.app.users.business.repository.model.UserDAO;
import com.app.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDAO, Long> {

    UserDAO findByUsername(String username);
    @Query("SELECT u FROM UserDAO u WHERE u.email = :email")
    UserDAO findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsById(Long userId);

    @Query(value = "SELECT * FROM users u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
            "OR (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))",
            nativeQuery = true)
    List<UserDAO> searchUsers(@Param("name") String name,
                              @Param("username") String username,
                              @Param("email") String email);



}

