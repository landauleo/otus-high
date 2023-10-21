package landau.leo.high.service;

import java.time.LocalDate;
import java.util.UUID;

import landau.leo.high.dao.UserDao;
import landau.leo.high.dto.Gender;
import landau.leo.high.dto.GetUserResponse;
import landau.leo.high.dto.LoginUserRequest;
import landau.leo.high.dto.RegisterUserRequest;
import landau.leo.high.entity.UserEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Disabled //comment when using docker
@Testcontainers
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0-debian"));

    @Test
    @DisplayName("Test authenticate user with incorrect password")
    void authenticateUserWithIncorrectPassword() {
        LoginUserRequest request = new LoginUserRequest(UUID.randomUUID().toString(), "lalal");
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(BCrypt.hashpw("i_am_wrong_hashed_pass", BCrypt.gensalt()));

        when(userDao.getById(request.getId())).thenReturn(userEntity);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> userService.authenticateUser(request));
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    @DisplayName("Test authenticate user with correct password")
    void authenticateUserWithCorrectPassword() {
        LoginUserRequest request = new LoginUserRequest(UUID.randomUUID().toString(), "i_am_pass");
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));

        when(userDao.getById(request.getId())).thenReturn(userEntity);

        assertDoesNotThrow(() -> userService.authenticateUser(request));
    }

    @Test
    @DisplayName("Test register user")
    void registerUser() {
        RegisterUserRequest request = new RegisterUserRequest(UUID.randomUUID(), "aa", "aa",
                LocalDate.now(), "aa", "aa", "aa");

        assertDoesNotThrow(() -> userService.registerUser(request));
    }

    @Test
    @DisplayName("Test get user by id")
    void getUserById() {
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "aa", "aa",
                LocalDate.now(), "aa", "aa", "aa", Gender.MALE);

        when(userDao.getById(anyString())).thenReturn(userEntity);

        assertDoesNotThrow(() -> userService.getUserById(UUID.randomUUID().toString()));
        assertEquals(userService.getUserById(UUID.randomUUID().toString()), GetUserResponse.toDto(userEntity));
    }

}