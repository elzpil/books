package com.app.users.business.repository;

import com.app.users.business.repository.model.UserDAO;
import com.app.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserDAO, Long> {

    UserDAO findByUsername(String username);
    @Query("SELECT u FROM UserDAO u WHERE u.email = :email")
    UserDAO findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsById(Long userId);
}

