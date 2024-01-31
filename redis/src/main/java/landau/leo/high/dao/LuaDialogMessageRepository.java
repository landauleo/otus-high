package landau.leo.high.dao;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import landau.leo.high.entity.DialogMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class LuaDialogMessageRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public List<DialogMessageEntity> findAllByFrom(UUID fromUserId) {
        String luaScript =
                "local fromUserId = ARGV[1]\n" +
                        "local messageIds = redis.call('SMEMBERS', 'messages:from:' .. fromUserId)\n" +
                        "local messages = {}\n" +
                        "for _, messageId in ipairs(messageIds) do\n" +
                        "    local messageData = redis.call('HGET', 'messages', messageId)\n" +
                        "    table.insert(messages, messageData)\n" +
                        "end\n" +
                        "return messages";

        List<String> result = redisTemplate.execute(
                new DefaultRedisScript<List>(luaScript, List.class),
                Collections.emptyList(),
                fromUserId.toString()
        );

        return result.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, DialogMessageEntity.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void save(DialogMessageEntity message) throws Exception {
        String luaScript =
                "local messageData = ARGV[1]\n" +
                        "local messageId = ARGV[2]\n" +
                        "local fromUserId = ARGV[3]\n" +
                        "redis.call('HSET', 'messages', messageId, messageData)\n" +
                        "redis.call('SADD', 'messages:from:' .. fromUserId, messageId)\n" +
                        "return true";

        String jsonMessage = objectMapper.writeValueAsString(message);
        String messageId = UUID.randomUUID().toString();
        String fromUserId = message.getFrom().toString();

        redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Boolean.class),
                Collections.emptyList(),
                jsonMessage, messageId, fromUserId
        );
    }
}
