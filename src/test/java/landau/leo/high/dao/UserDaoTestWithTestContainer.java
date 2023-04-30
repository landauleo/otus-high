package landau.leo.high.dao;

import java.time.LocalDate;
import java.util.UUID;

import landau.leo.high.dto.Gender;
import landau.leo.high.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
public class UserDaoTestWithTestContainer {

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0-debian"));

    @Autowired
    private UserDao userDao;

    @Test
    @DisplayName("Test insert and getById user")
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

}
