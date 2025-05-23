package com.app.users.controller;

import com.app.users.auth.util.JwtTokenUtil;
import com.app.users.business.service.UserService;
import com.app.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import com.app.users.dto.ChangePasswordRequest;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserController userController;

    private User user;
    private final Long userId = 1L;
    private final String token = "Bearer mockedToken";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setRole("USER");
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());

        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void getUserById_UserExists_ShouldReturnUser() {
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_UserNotFound_ShouldReturnNotFound() {
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void updateUser_Authorized_ShouldReturnUpdatedUser() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.updateUser(userId, user, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService, times(1)).updateUser(userId, user);
    }

    @Test
    void updateUser_Unauthorized_ShouldReturnForbidden() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(2L);

        ResponseEntity<User> response = userController.updateUser(userId, user, token);

        assertEquals(FORBIDDEN, response.getStatusCode());
        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    void deleteUser_Authorized_ShouldReturnNoContent() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(userService.deleteUser(userId)).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser(userId, token);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_Unauthorized_ShouldReturnForbidden() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(2L);

        ResponseEntity<Void> response = userController.deleteUser(userId, token);

        assertEquals(FORBIDDEN, response.getStatusCode());
        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(user));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUserRole_UserExists_ShouldReturnUpdatedUser() {
        when(userService.updateUserRole(userId, "ADMIN")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.updateUserRole(userId, "ADMIN");

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService, times(1)).updateUserRole(userId, "ADMIN");
    }

    @Test
    void updateUserRole_UserNotFound_ShouldReturnNotFound() {
        when(userService.updateUserRole(userId, "ADMIN")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.updateUserRole(userId, "ADMIN");

        assertEquals(NOT_FOUND, response.getStatusCode());

        verify(userService, times(1)).updateUserRole(userId, "ADMIN");
    }

    @Test
    void checkUserExists_ShouldReturnTrue() {
        when(userService.userExists(userId)).thenReturn(true);

        ResponseEntity<Boolean> response = userController.checkUserExists(userId);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(userService, times(1)).userExists(userId);
    }

    @Test
    void checkUserExists_ShouldReturnFalse() {
        when(userService.userExists(userId)).thenReturn(false);

        ResponseEntity<Boolean> response = userController.checkUserExists(userId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody());

        verify(userService, times(1)).userExists(userId);
    }

    @Test
    void changePassword_AuthorizedAndCorrectOldPassword_ShouldSucceed() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(userService.changePassword(userId, "oldPass", "newPass")).thenReturn(true);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        ResponseEntity<String> response = userController.changePassword(userId, request, token);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
        verify(userService).changePassword(userId, "oldPass", "newPass");
    }

    @Test
    void changePassword_AuthorizedButIncorrectOldPassword_ShouldReturnBadRequest() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(userService.changePassword(userId, "wrongOld", "newPass")).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongOld");
        request.setNewPassword("newPass");
        ResponseEntity<String> response = userController.changePassword(userId, request, token);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Old password is incorrect", response.getBody());
        verify(userService).changePassword(userId, "wrongOld", "newPass");
    }

    @Test
    void changePassword_Unauthorized_ShouldReturnForbidden() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(2L);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        ResponseEntity<String> response = userController.changePassword(userId, request, token);

        assertEquals(FORBIDDEN, response.getStatusCode());
        assertEquals("Unauthorized to change this password", response.getBody());
        verify(userService, never()).changePassword(anyLong(), anyString(), anyString());
    }
    @Test
    void searchUsers_WithAllParams_ShouldReturnResults() {
        when(userService.searchUsers("John", "john123", "john@example.com"))
                .thenReturn(List.of(user));

        ResponseEntity<List<User>> response = userController.searchUsers("John", "john123", "john@example.com");

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(user.getId(), response.getBody().get(0).getId());
    }

    @Test
    void searchUsers_WithPartialParams_ShouldReturnResults() {
        when(userService.searchUsers(null, "john123", null))
                .thenReturn(List.of(user));

        ResponseEntity<List<User>> response = userController.searchUsers(null, "john123", null);

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void searchUsers_NoParams_ShouldReturnEmptyList() {
        when(userService.searchUsers(null, null, null)).thenReturn(List.of());

        ResponseEntity<List<User>> response = userController.searchUsers(null, null, null);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getUserEmail_UserExists_ShouldReturnEmail() {
        user.setEmail("test@example.com");
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<String> response = userController.getUserEmail(userId);

        assertEquals(OK, response.getStatusCode());
        assertEquals("test@example.com", response.getBody());
    }

    @Test
    void getUserEmail_UserNotFound_ShouldReturnNotFound() {
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.getUserEmail(userId);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


}
