package landau.leo.high.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import landau.leo.high.entity.PostAuditEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostAuditDao {

    private final NamedParameterJdbcOperations jdbcTemplate;

    private static final String INSERT_POST_AUDIT = "INSERT INTO app.post_audit (id, user_id, created_date) VALUES (:id, :userId, :createdDate)";

    private static final String DELETE_POST_AUDIT_BY_ID = "DELETE FROM app.post_audit WHERE id = :id";

    private static final String GET_POST_AUDIT_BY_ID = "SELECT * FROM app.post_audit WHERE id = :id";

    public void deletePostAudit(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        jdbcTemplate.update(DELETE_POST_AUDIT_BY_ID, parameters);
    }

    public void insertPostAudit(PostAuditEntity postAuditEntity) {
        Map<String, ? extends Serializable> values = Map.of(
                "id", String.valueOf(postAuditEntity.getId()),
                "userId", postAuditEntity.getUserId(),
                "createdDate", Timestamp.from(postAuditEntity.getCreatedDate().toInstant()));
        jdbcTemplate.update(INSERT_POST_AUDIT, values);
    }

    public PostAuditEntity getPostAuditById(String id) {
        Map<String, String> values = Map.of("id", id);
        try {
            return jdbcTemplate.queryForObject(GET_POST_AUDIT_BY_ID, values, new PostAuditMapper());
        } catch (Exception ex) {
            log.error("Failed to get postAudit", ex);
            throw new RuntimeException("Failed to get postAudit");
        }
    }

    private static class PostAuditMapper implements RowMapper<PostAuditEntity> {

        @Override
        public PostAuditEntity mapRow(ResultSet rs, int i) throws SQLException {
            return new PostAuditEntity(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("user_id")),
                    rs.getTimestamp("created_date").toInstant().atZone(ZoneId.of("Europe/Moscow"))
            );
        }

    }

}
