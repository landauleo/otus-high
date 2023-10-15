package landau.leo.high.dao;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import landau.leo.high.dto.Gender;
import landau.leo.high.entity.UserEntity;
import landau.leo.high.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDaoTestWithoutTestContainerTest {

    @Autowired
    private UserDao userDao;

    @MockBean
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FIRST_NAME = "Максим";
    private static final String SECOND_NAME = "Носов";

    @Test
    @DisplayName("Test insert and get user")
    void insertTest() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity
                .builder()
                .id(userId)
                .firstName("firstName")
                .secondName("secondName")
                .biography("I'm a robot")
                .birthdate(LocalDate.of(1997, 4, 11))
                .city("Beijing")
                .gender(Gender.FEMALE)
                .password("ararar123")
                .build();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userDao.getById(userId.toString()));

        userDao.insert(userEntity);

        assertEquals(userEntity, userDao.getById(String.valueOf(userId)));
    }

    @Test
    @DisplayName("Test insert and get user")
    void getByFirstAndSecondNameNonExistingUserTest() {
        assertDoesNotThrow(() ->  userDao.getByFirstAndSecondName("конь", "в пальто"));
    }

    @Test
    @DisplayName("Test time for user load")
    void loadDefaultUsersTest() {
        LocalDateTime start = LocalDateTime.now();
        assertDoesNotThrow(() -> userDao.loadDefaultUsers());
        LocalDateTime end = LocalDateTime.now();
        System.out.printf("Duration of users loading took: %s seconds%n", Duration.between(start, end).getSeconds());
 }

    @Test
    @Transactional
    @DisplayName("Test getting user by name with various number of requests")
    void getByFirstAndSecondNameTest() {
        userDao.loadDefaultUsers();

        //1 user
        LocalDateTime start = LocalDateTime.now();
        userDao.getByFirstAndSecondName(FIRST_NAME, SECOND_NAME);
        LocalDateTime end = LocalDateTime.now();
        System.out.printf("Duration of 1 user search took: %s nanoseconds%n", Duration.between(start, end).getNano());

        //10 users
        start = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            assertNotNull(userDao.getByFirstAndSecondName(FIRST_NAME, SECOND_NAME));
        }
        end = LocalDateTime.now();
        System.out.printf("Duration of 10 user search took: %s seconds%n", Duration.between(start, end).getNano());

        //100 users
        start = LocalDateTime.now();
        for (int i = 0; i < 100; i++) {
            assertNotNull(userDao.getByFirstAndSecondName(FIRST_NAME, SECOND_NAME));
        }
        end = LocalDateTime.now();
        System.out.printf("Duration of 100 user search took: %s seconds%n", Duration.between(start, end).getNano());

        //1000 users
        start = LocalDateTime.now();
        for (int i = 0; i < 1000; i++) {
            assertNotNull(userDao.getByFirstAndSecondName(FIRST_NAME, SECOND_NAME));
        }
        end = LocalDateTime.now();
        System.out.printf("Duration of 1000 user search took: %s seconds%n", Duration.between(start, end).getNano());
    }

}