package landau.leo.high.dao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import landau.leo.high.entity.PostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
public class LuaPostRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public List<PostEntity> findAllWithOffSetAndLimit(int offset, long limit) throws Exception {
        String luaScript =
                "local offset = tonumber(ARGV[1])\n" +
                        "local limit = tonumber(ARGV[2])\n" +
                        "local postIds = redis.call('LRANGE', 'posts', offset, offset + limit - 1)\n" +
                        "local posts = {}\n" +
                        "for _, postId in ipairs(postIds) do\n" +
                        "    local postData = redis.call('HGET', 'posts_data', postId)\n" +
                        "    table.insert(posts, postData)\n" +
                        "end\n" +
                        "return posts";

        List<String> result = redisTemplate.execute(new DefaultRedisScript<>(luaScript, List.class), Collections.emptyList(), String.valueOf(offset), String.valueOf(limit));

        return result.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, PostEntity.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void save(PostEntity entity) throws Exception {
        String luaScript =
                "local postData = ARGV[1]\n" +
                        "local postId = ARGV[2]\n" +
                        "redis.call('HSET', 'posts_data', postId, postData)\n" +
                        "redis.call('LPUSH', 'posts', postId)\n" +
                        "return true";

        String jsonEntity = objectMapper.writeValueAsString(entity);
        String postId = entity.getId().toString();

        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class), Collections.emptyList(), jsonEntity, postId);
    }

    public void deleteAll() {
        String luaScript =
                "redis.call('DEL', 'posts_data')\n" +
                        "redis.call('DEL', 'posts')\n" +
                        "return true";

        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class), Collections.emptyList());
    }

    public void saveAll(List<PostEntity> list) throws Exception {
        for (PostEntity entity : list) {
            save(entity);
        }
    }

}

