package com.app.users.business.repository;

import com.app.users.business.repository.model.UserDAO;
import com.app.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDAO, Long> {

    UserDAO findByUsername(String username);
    UserDAO findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsById(Long userId);
}

