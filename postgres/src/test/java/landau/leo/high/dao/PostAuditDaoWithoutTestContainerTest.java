package landau.leo.high.dao;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import landau.leo.high.entity.PostAuditEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostAuditDaoWithoutTestContainerTest {

    @Autowired
    private PostAuditDao postAuditDao;

    @Test
    @DisplayName("Test insert and get post audit")
    void insertTest() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PostAuditEntity postAudit = new PostAuditEntity(id, userId, ZonedDateTime.of(2000, 2, 2, 0, 0, 0, 0, ZoneId.of("Europe/Moscow")));

        postAuditDao.insertPostAudit(postAudit);

        assertEquals(postAudit, postAuditDao.getPostAuditById(String.valueOf(id)));
    }

}