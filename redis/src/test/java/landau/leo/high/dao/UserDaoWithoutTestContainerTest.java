package landau.leo.high.dao;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import landau.leo.high.dto.Gender;
import landau.leo.high.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserDaoWithoutTestContainerTest {

    @Autowired
    private LuaUserRepository userRepository;

    private static final String FIRST_NAME = "Максим";
    private static final String SECOND_NAME = "Носов";

    @Test
    @DisplayName("Test insert and get user")
    void insertTest() throws Exception {
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

        userRepository.save(userEntity);

        assertEquals(Optional.of(userEntity), userRepository.findById(userId));
    }

    @Test
    @DisplayName("Test insert and get user")
    void getByFirstAndSecondNameNonExistingUserTest() {
        assertDoesNotThrow(() -> userRepository.findAllByFirstNameAndSecondName(FIRST_NAME, SECOND_NAME));
    }

    @Test
    @DisplayName("Test count all users")
    void countAllUsersTest() throws Exception {
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

        userRepository.save(userEntity);

        assertTrue(userRepository.count() > 0);
    }

    @Test
    @DisplayName("Test delete all users")
    void deleteAllUsersTest() {
        assertDoesNotThrow(() -> userRepository.deleteAll());

        assertEquals(0, userRepository.count());
    }

}