package landau.leo.high.dao;

import java.time.LocalDate;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDaoTestWithoutTestContainer {

    @Autowired
    private UserDao userDao;

    @MockBean
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Test insert user")
    void insert() {
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
    void getById() {
    }

}