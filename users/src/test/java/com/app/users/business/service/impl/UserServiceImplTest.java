package com.app.users.business.service.impl;

import com.app.users.business.mapper.UserMapper;
import com.app.users.business.repository.UserRepository;
import com.app.users.business.repository.model.UserDAO;
import com.app.users.exception.ResourceNotFoundException;
import com.app.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");

        userDAO = new UserDAO();
        userDAO.setId(1L);
        userDAO.setEmail("test@example.com");
        userDAO.setPassword("password123");
    }

    @Test
    void testSaveUser() {
        when(userMapper.userToUserDAO(user)).thenReturn(userDAO);
        when(userRepository.save(userDAO)).thenReturn(userDAO);
        when(userMapper.userDAOToUser(userDAO)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userMapper.userDAOToUser(userDAO)).thenReturn(user);

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteUser_UserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertTrue(userService.deleteUser(1L));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testFindByEmail_UserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(userDAO);
        when(userMapper.userDAOToUser(userDAO)).thenReturn(user);

        Optional<User> foundUser = userService.findByEmail(user.getEmail());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void testFindByEmail_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        Optional<User> foundUser = userService.findByEmail(user.getEmail());

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername(user.getEmail())).thenReturn(true);

        assertTrue(userService.existsByUsername(user.getEmail()));
    }

    @Test
    void testUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertTrue(userService.userExists(1L));
    }
    @Test
    void testUpdateUser_SuccessfulUpdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.findByUsername(nullable(String.class))).thenReturn(null);
        when(userRepository.save(any())).thenReturn(userDAO);
        when(userMapper.userDAOToUser(any())).thenReturn(user);

        user.setName("Updated Name");
        Optional<User> result = userService.updateUser(1L, user);

        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
        verify(userRepository).save(any());
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        UserDAO existing = new UserDAO();
        existing.setId(2L);
        existing.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(existing);
        user.setEmail("existing@example.com");

        Optional<User> result = userService.updateUser(1L, user);
        assertFalse(result.isPresent());
        verify(userRepository).save(any());
    }

    @Test
    void testUpdateUser_UsernameAlreadyExists() {
        UserDAO existing = new UserDAO();
        existing.setUsername("existingUser");

        user.setUsername("existingUser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userRepository.findByUsername("existingUser")).thenReturn(existing);

        Optional<User> result = userService.updateUser(1L, user);
        assertFalse(result.isPresent());
        verify(userRepository).save(any());
    }

    @Test
    void testUpdateUserRole_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userRepository.save(any())).thenReturn(userDAO);
        when(userMapper.userDAOToUser(userDAO)).thenReturn(user);

        Optional<User> result = userService.updateUserRole(1L, "ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", userDAO.getRole());
    }

    @Test
    void testUpdateUserRole_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUserRole(1L, "ADMIN");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UserFound() {
        userDAO.setUsername("user123");
        when(userRepository.findByUsername("user123")).thenReturn(userDAO);

        User found = userService.findByUsername("user123");

        assertNotNull(found);
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("missingUser")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findByUsername("missingUser"));
    }

    @Test
    void testChangePassword_Successful() {
        userDAO.setPassword("encodedOldPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        boolean result = userService.changePassword(1L, "oldPassword", "newPassword");

        assertTrue(result);
        assertEquals("encodedNewPassword", userDAO.getPassword());
        verify(userRepository).save(userDAO);
    }

    @Test
    void testChangePassword_IncorrectOldPassword() {
        userDAO.setPassword("encodedOldPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userDAO));
        when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

        boolean result = userService.changePassword(1L, "wrongOldPassword", "newPassword");

        assertFalse(result);
    }

    @Test
    void testChangePassword_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.changePassword(1L, "old", "new"));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userDAO));
        when(userMapper.userDAOToUser(userDAO)).thenReturn(user);

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(user.getEmail(), users.get(0).getEmail());
    }

    @Test
    void testExistsByEmail_True() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(userDAO);

        assertTrue(userService.existsByEmail(user.getEmail()));
    }

    @Test
    void testExistsByEmail_False() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        assertFalse(userService.existsByEmail(user.getEmail()));
    }

    @Test
    void testSearchUsers() {
        when(userRepository.searchUsers("John", "johnny", "john@example.com"))
                .thenReturn(List.of(userDAO));
        when(userMapper.userDAOToUser(userDAO)).thenReturn(user);

        List<User> results = userService.searchUsers("John", "johnny", "john@example.com");

        assertEquals(1, results.size());
        assertEquals(user.getEmail(), results.get(0).getEmail());
    }

}
