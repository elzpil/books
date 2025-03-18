package com.app.users.business.service.impl;

import com.app.users.business.mapper.UserMapper;
import com.app.users.business.repository.UserRepository;
import com.app.users.business.repository.model.UserDAO;
import com.app.users.business.service.UserService;
import com.app.users.exception.ResourceNotFoundException;
import com.app.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user: {}", user.getEmail());
        UserDAO userDAO = userMapper.userToUserDAO(user);
        userDAO = userRepository.save(userDAO);
        return userMapper.userDAOToUser(userDAO);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        return userRepository.findById(userId).map(userMapper::userDAOToUser);
    }

    @Transactional
    @Override
    public Optional<User> updateUser(Long userId, User updatedUser) {
        log.info("Updating user with ID: {}", userId);

        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
            existingUser.setBio(updatedUser.getBio());
            existingUser.setRole(updatedUser.getRole());
            UserDAO updatedDAO = userRepository.save(existingUser);
            return userMapper.userDAOToUser(updatedDAO);
        });
    }

    @Override
    public boolean deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        userRepository.deleteById(userId);
        return true;
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::userDAOToUser)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> updateUserRole(Long userId, String roleName) {

        Optional<UserDAO> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        UserDAO userDAO = userOpt.get();

        userDAO.setRole(roleName);

        userRepository.save(userDAO);
        User user = userMapper.userDAOToUser(userDAO);

        return Optional.of(user);
    }

    @Override
    public User findByUsername(String username) {
        Optional<UserDAO> userDAO = Optional.ofNullable(userRepository.findByUsername(username));
        return userDAO.map(u -> new User(u.getEmail(), u.getPassword()))
                .orElseThrow(() -> new ResourceNotFoundException("Username", 0L)); //TODO fix this
    }

    @Override
    public User findByEmail(String email) {
        Optional<UserDAO> userDAO = Optional.ofNullable(userRepository.findByEmail(email));
        return userDAO.map(u -> new User(u.getEmail(), u.getPassword()))
                .orElseThrow(() -> new ResourceNotFoundException("Email", email)); //TODO fix this
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
}
