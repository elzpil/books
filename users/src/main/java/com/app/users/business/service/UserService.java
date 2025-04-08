package com.app.users.business.service;

import com.app.users.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> getUserById(Long userId);
    Optional<User> updateUser(Long userId, User updatedUser);
    boolean deleteUser(Long userId);
    List<User> getAllUsers();
    Optional<User> updateUserRole(Long userId, String roleName);

    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean userExists(Long userId);

    boolean changePassword(Long userId, String oldPassword, String newPassword);

    List<User> searchUsers(String name, String username, String email);

}
