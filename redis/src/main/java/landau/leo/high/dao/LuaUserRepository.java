package landau.leo.high.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import landau.leo.high.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
public class LuaUserRepository {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /*
    by default ObjectMapper do not understand the LocalDate -> added dependency jackson-datatype-jsr310 + registered datatype module
     */
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void save(UserEntity user) throws Exception {
        String luaScript =
                "local userId = KEYS[1]\n" + //объявляет локальную переменную, KEYS - ключи, переданные в скрипт
                        "local userData = ARGV[1]\n" + //ARGV - аргументы, переданные в скрипт
                        "redis.call('HSET', 'users', userId, userData)\n" + //HSET - команда для сохранения пары ключ:хэш-значение, users - имя хэша, userId - ключ, userData - значение
                        "return userId";

        String jsonUser = objectMapper.writeValueAsString(user);
        redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, String.class),
                Collections.singletonList(user.getId().toString()),
                jsonUser
        );
    }

    public List<UserEntity> findAllByFirstNameAndSecondName(String firstName, String secondName) {
        String luaScript =
                "local firstName = ARGV[1]\n" +
                        "local secondName = ARGV[2]\n" +
                        "local setKey = firstName .. ':' .. secondName\n" + //создает ключ с помощью конкатенации firstName:secondName
                        "local userIds = redis.call('SMEMBERS', setKey)\n" + //SMEMBERS - команда Redis, которая возвращает все элементы указанного множества
                        "local users = {}\n" +
                        "for _, userId in ipairs(userIds) do\n" +
                        "    local userData = redis.call('HGET', 'users', userId)\n" +
                        "    table.insert(users, userData)\n" +
                        "end\n" +
                        "return users";

        List<String> result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, List.class),
                Collections.emptyList(),
                firstName, secondName
        );

        return result.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, UserEntity.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public Optional<UserEntity> findById(UUID id) throws Exception {
        String luaScript =
                "local userId = KEYS[1]\n" +
                        "local userData = redis.call('HGET', 'users', userId)\n" +
                        "return userData";

        String result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, String.class),
                Collections.singletonList(id.toString())
        );

        return Optional.ofNullable(objectMapper.readValue(result, UserEntity.class));
    }

    public long count() {
        String luaScript = "return redis.call('HLEN', 'users')";
        Long result = redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Collections.emptyList());
        return result != null ? result : 0;
    }

    public void deleteAll() {
        String luaScript = "redis.call('DEL', 'users')\nreturn true";
        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class), Collections.emptyList());
    }

    public void saveAll(List<UserEntity> users) throws Exception {
        for (UserEntity user : users) {
            save(user);
        }
    }

}
