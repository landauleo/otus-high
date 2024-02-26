package landau.leo.high.dao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import javax.sql.DataSource;

import landau.leo.high.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostDao {

    private final NamedParameterJdbcOperations jdbcTemplate;
    private final DataSource dataSource;

    //открытием стало то, что сначала всегда идет лимит и только потом оффсет, иначе запрос крашится
    private static final String GET_FRIENDS_POSTS = "SELECT id, post_text, user_id FROM post LIMIT :limit OFFSET :offset";

    private static final String INSERT_POST = "INSERT INTO post (id, post_text, user_id) VALUES (:id, :postText, :userId)";

    private static final String DELETE_POST_BY_ID = "DELETE FROM post WHERE id = :id";

    public void deletePost(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        jdbcTemplate.update(DELETE_POST_BY_ID, parameters);
    }

    public List<PostEntity> getFriendsPosts(int offset, long limit) {
        try {
            List<PostEntity> friendsPosts = new ArrayList<>();
            List<Map<String, Object>> list = jdbcTemplate.queryForList(GET_FRIENDS_POSTS, Map.of("offset", offset, "limit", limit));
            list.forEach(m -> {
                PostEntity post = new PostEntity(UUID.fromString((String) m.get("id")), UUID.fromString((String) m.get("user_id")), (String) m.get("post_text"));
                friendsPosts.add(post);
            });
            return friendsPosts;

        } catch (Exception ex) {
            log.error("Failed to get friends posts", ex);
            throw new RuntimeException("Failed to get friends posts", ex);
        }
    }

    public void insertPost(PostEntity post) {
        Map<String, ? extends Serializable> values = Map.of(
                "id", String.valueOf(post.getId()),
                "postText", post.getText(),
                "userId", String.valueOf(post.getId())
        );
        jdbcTemplate.update(INSERT_POST, values);
    }

    @SneakyThrows
    public void loadDefaultPosts() {
        new File("posts.txt");
        InputStream is = getClass().getClassLoader().getResourceAsStream("posts.txt");
        BufferedInputStream bis = new BufferedInputStream(is, 1024 * 1024);

        jdbcTemplate.update("TRUNCATE TABLE app.post;", Collections.emptyMap());

        Scanner s = new Scanner(bis);
        String text;
        String id;
        int pos = 0;
        String userId = "2fcb454f-2e39-4a23-9a0f-27fcaff0ee66";
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        log.info("Is about to insert default posts");
        LocalDateTime parseStart = LocalDateTime.now();
        try (PreparedStatement ps = connection.prepareStatement(String.format("INSERT INTO post (id, post_text, user_id) VALUES (%s, %s, %s);", "?", "?", "?"))) {

            while (s.hasNextLine()) {
                id = String.valueOf(UUID.randomUUID());
                text = s.nextLine();
                ps.setString(1, id);
                ps.setString(2, text);
                ps.setString(3, userId);
                ps.addBatch();
                pos++;
                if (pos % 1000 == 0) log.info(pos + " rows to be inserted");
            }
            log.info(pos + " rows are gonna be batch-inserted");
            ps.executeBatch();

            log.info(pos + " were successfully batch-inserted");
        } catch (Exception e) {
            log.error("failed at row " + pos, e);
            throw e;
        } finally {
            connection.commit();
            connection.close();
            log.info("Transaction is committed and closed");
        }

        LocalDateTime parseEnd = LocalDateTime.now();
        log.info("Duration of insert default posts: {} seconds", Duration.between(parseStart, parseEnd).getSeconds());
    }

}
