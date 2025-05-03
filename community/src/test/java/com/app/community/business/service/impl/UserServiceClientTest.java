import com.app.community.business.service.impl.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceClient userServiceClient;

    private final String baseUrl = "http://localhost:8081/users/exists/";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userServiceClient = new UserServiceClient(restTemplate);

        // 👇 Use reflection to set the private field "userExistsUrl"
        Field field = UserServiceClient.class.getDeclaredField("userExistsUrl");
        field.setAccessible(true);
        field.set(userServiceClient, baseUrl);
    }

    @Test
    void doesUserExist_UserExists_ReturnsTrue() {
        String url = baseUrl + userId;
        when(restTemplate.getForEntity(url, Void.class)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean exists = userServiceClient.doesUserExist(userId);

        assertTrue(exists);
        verify(restTemplate, times(1)).getForEntity(url, Void.class);
    }

    @Test
    void doesUserExist_UserDoesNotExist_ReturnsFalse() {
        String url = baseUrl + userId;
        when(restTemplate.getForEntity(url, Void.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean exists = userServiceClient.doesUserExist(userId);

        assertFalse(exists);
        verify(restTemplate, times(1)).getForEntity(url, Void.class);
    }

    @Test
    void doesUserExist_ClientErrorStatus_ReturnsFalse() {
        String url = baseUrl + userId;
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(url, Void.class)).thenReturn(response);

        boolean exists = userServiceClient.doesUserExist(userId);

        assertFalse(exists);
        verify(restTemplate, times(1)).getForEntity(url, Void.class);
    }
}
