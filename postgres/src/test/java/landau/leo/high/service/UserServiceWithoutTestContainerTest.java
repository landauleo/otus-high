package landau.leo.high.service;

import landau.leo.high.dao.PostDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceWithoutTestContainerTest {

    @MockBean
    private PostDao postDao;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Test insert and get user")
    void testCachedUsersPosts() {
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(() -> userService.getUsersPosts(2, 10));
        }

        then(postDao).should(times(1)).getFriendsPosts(2, 10);
    }


}