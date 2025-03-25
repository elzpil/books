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
}
