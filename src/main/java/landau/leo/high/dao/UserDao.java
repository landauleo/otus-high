package landau.leo.high.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import landau.leo.high.dto.Gender;
import landau.leo.high.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDao {

    private final NamedParameterJdbcOperations jdbcTemplate;

    private final static String INSERT_USER = "INSERT INTO user (id, first_name, second_name, birthdate, biography, city, password, gender) " +
            "VALUES (:id, :firstName, :secondName, :birthdate, :biography, :city, :password, :gender)";

    private final static String GET_USER_BY_ID = "SELECT id, first_name, second_name, birthdate, biography, city, password, gender FROM user WHERE id = :id";

    public void insert(UserEntity user) {
        Map<String, ? extends Serializable> values = Map.of(
                "id", String.valueOf(user.getId()),
                "firstName", user.getFirstName(),
                "secondName", user.getSecondName(),
                "birthdate", user.getBirthdate(),
                "biography", user.getBiography(),
                "city", user.getCity(),
                "password", user.getPassword(),
                "gender", String.valueOf(user.getGender())
        );
        jdbcTemplate.update(INSERT_USER, values);
    }

    public UserEntity getById(String id) {
        Map<String, String> values = Map.of("id", id);
        try {
            return jdbcTemplate.queryForObject(GET_USER_BY_ID, values, new UserMapper());
        } catch (Exception ex) {
            log.error("Failed to insert user", ex);
            throw new AuthenticationCredentialsNotFoundException("Invalid id");
        }
    }

    private static class UserMapper implements RowMapper<UserEntity> {

        @Override
        public UserEntity mapRow(ResultSet rs, int i) throws SQLException {
            return new UserEntity(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("first_name"),
                    rs.getString("second_name"),
                    rs.getDate("birthdate").toLocalDate(),
                    rs.getString("biography"),
                    rs.getString("city"),
                    rs.getString("password"),
                    Gender.valueOf(rs.getString("gender"))
            );
        }
    }

}
