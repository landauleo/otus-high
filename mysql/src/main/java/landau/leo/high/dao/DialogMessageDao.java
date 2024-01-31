package landau.leo.high.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import landau.leo.high.entity.DialogMessageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DialogMessageDao {

    private final NamedParameterJdbcOperations jdbcTemplate;

    private static final String GET_MESSAGES = "SELECT from_user_id, to_user_id, text_message FROM app.dialog_message WHERE from_user_id = :userId OR to_user_id = :userId";
    private static final String INSERT_TEXT_MESSAGE = "INSERT INTO app.dialog_message (from_user_id, to_user_id, text_message) " +
            "VALUES (:fromUserId, :toUserId, :textMessage)";

    public List<DialogMessageEntity> getMessages(String userId) {
        try {
            List<DialogMessageEntity> messages = new ArrayList<>();
            List<Map<String, Object>> list = jdbcTemplate.queryForList(GET_MESSAGES, Map.of("userId", userId));
            list.forEach(m -> {
                DialogMessageEntity post = new DialogMessageEntity(UUID.fromString((String) m.get("from_user_id")),
                        UUID.fromString((String) m.get("from_user_id")), (String) m.get("text_message"));
                messages.add(post);
            });
            return messages;

        } catch (Exception ex) {
            log.error("Failed to get messages", ex);
            throw new RuntimeException("Failed to get messages", ex);
        }
    }

    public void insert(DialogMessageEntity entity) {
        Map<String, ? extends Serializable> values = Map.of(
                "fromUserId", String.valueOf(entity.getFrom()),
                "toUserId", String.valueOf(entity.getTo()),
                "textMessage", entity.getText());

        jdbcTemplate.update(INSERT_TEXT_MESSAGE, values);
    }

}
