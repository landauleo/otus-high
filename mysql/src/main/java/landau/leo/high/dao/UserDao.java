package landau.leo.high.dao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import javax.sql.DataSource;

import landau.leo.high.dto.Gender;
import landau.leo.high.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDao {

    private final NamedParameterJdbcOperations jdbcTemplate;
    private final DataSource dataSource;

    private static final String INSERT_USER = "INSERT INTO user (id, first_name, second_name, birthdate, biography, city, password, gender) " +
            "VALUES (:id, :firstName, :secondName, :birthdate, :biography, :city, :password, :gender)";

    private static final String GET_USER_BY_ID = "SELECT id, first_name, second_name, birthdate, biography, city, password, gender FROM user WHERE id = :id";

    private static final String COUNT_USERS = "SELECT COUNT(id) FROM user;";

    private static final String GET_USER_BY_FIRST_AND_SECOND_NAME = "SELECT id, first_name, second_name, birthdate, biography, city, password, gender FROM user" +
            " WHERE first_name LIKE :firstName AND second_name LIKE :secondName;";

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

    public UserEntity getUserById(String id) {
        Map<String, String> values = Map.of("id", id);
        try {
            return jdbcTemplate.queryForObject(GET_USER_BY_ID, values, new UserMapper());
        } catch (Exception ex) {
            log.error("Failed to insert user", ex);
            throw new RuntimeException("Invalid id");
        }
    }

    public Integer getUsersNumber() {
        try {
            return jdbcTemplate.queryForObject(COUNT_USERS, Collections.emptyMap(), Integer.class);
        } catch (Exception ex) {
            log.error("Failed to count users", ex);
            throw new RuntimeException("Failed to count users");
        }
    }

    public List<UserEntity> getByFirstAndSecondName(String firstName, String secondName) {
        Map<String, String> values = Map.of("firstName", firstName.trim().toLowerCase(Locale.ROOT) + "%", "secondName", secondName.toLowerCase(Locale.ROOT) + "%");
        List<UserEntity> userEntityList = new ArrayList<>();
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(GET_USER_BY_FIRST_AND_SECOND_NAME, values);
            list.forEach(m -> {
                UserEntity user = new UserEntity();

                Date birthdate = (Date) m.get("birthdate");
                String gender = (String) m.get("gender");

                user.setId(UUID.fromString((String) m.get("id")));
                user.setFirstName((String) m.get("first_name"));
                user.setSecondName((String) m.get("second_name"));
                user.setBirthdate(birthdate == null ? null : birthdate.toLocalDate());
                user.setBiography((String) m.get("biography"));
                user.setCity((String) m.get("city"));
                user.setPassword((String) m.get("password"));
                user.setGender(gender == null ? null : Gender.valueOf(gender));

                userEntityList.add(user);
            });
        } catch (Exception ex) {
            log.error("Failed to get user by first and second name", ex);
            throw new RuntimeException(ex);
        }
        return userEntityList;
    }

    @SneakyThrows
    public void loadDefaultUsers() {
        new File("people.csv");
        InputStream is = getClass().getClassLoader().getResourceAsStream("people.csv");
        BufferedInputStream bis = new BufferedInputStream(is, 1024 * 1024);

        jdbcTemplate.update("TRUNCATE TABLE dialog_message;", Collections.emptyMap());
        jdbcTemplate.update("ALTER TABLE dialog_message DROP FOREIGN KEY dialog_message_ibfk_1;", Collections.emptyMap());
        jdbcTemplate.update("ALTER TABLE dialog_message DROP FOREIGN KEY dialog_message_ibfk_2;", Collections.emptyMap());
        jdbcTemplate.update("TRUNCATE TABLE user;", Collections.emptyMap());
        jdbcTemplate.update("DROP INDEX user_first_name_idx ON USER;", Collections.emptyMap());
        jdbcTemplate.update("DROP INDEX user_second_name_idx ON USER;", Collections.emptyMap());

        Scanner s = new Scanner(bis);
        String firstName = "";
        String secondName = "";
        String id;
        int pos = 0;
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        log.info("Is about to insert default users");
        LocalDateTime parseStart = LocalDateTime.now();
        try (PreparedStatement ps = connection.prepareStatement(String.format("INSERT INTO user (id, first_name, second_name) VALUES (%s, %s, %s);", "?", "?", "?"))) {

            while (s.hasNextLine()) {
                id = String.valueOf(UUID.randomUUID());
                secondName = s.next().trim().toLowerCase(Locale.ROOT);
                firstName = s.nextLine().split(",")[0].trim().toLowerCase(Locale.ROOT);
                ps.setString(1, id);
                ps.setString(2, firstName);
                ps.setString(3, secondName);
                ps.addBatch();
                pos++;
                if (pos % 1000 == 0) log.info(pos + " rows to be inserted");
            }
            log.info(pos + " rows are gonna be batch-inserted");
            ps.executeBatch();

            log.info(pos + " were successfully batch-inserted");
        } catch (Exception e) {
            log.error("firstName: " + firstName + " secondName: " + secondName);
            log.error("failed at row " + pos, e);
            throw e;
        } finally {
            connection.commit();
            connection.close();
            log.info("Transaction is committed and closed");
        }

        log.info("Indexes are going to be created");

        //https://dev.mysql.com/doc/refman/8.0/en/create-index.html
        jdbcTemplate.update("CREATE INDEX user_first_name_idx ON user (first_name) USING BTREE;", Collections.emptyMap());
//        jdbcTemplate.update("CREATE INDEX user_first_name_idx ON user (first_name) USING HASH;", Collections.emptyMap());
        log.info("user_first_name_idx is created");

        jdbcTemplate.update("CREATE INDEX user_second_name_idx ON user (second_name) USING BTREE;", Collections.emptyMap());
//        jdbcTemplate.update("CREATE INDEX user_second_name_idx ON user (second_name) USING HASH;", Collections.emptyMap());
        log.info("user_second_name_idx is created");

        LocalDateTime parseEnd = LocalDateTime.now();
        log.info("Duration of insert default users: {} seconds", Duration.between(parseStart, parseEnd).getSeconds());
    }

    private static class UserMapper implements RowMapper<UserEntity> {

        @Override
        public UserEntity mapRow(ResultSet rs, int i) throws SQLException {
            String gender = rs.getString("gender");
            Date birthdate = rs.getDate("birthdate");
            return new UserEntity(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("first_name"),
                    rs.getString("second_name"),
                    birthdate == null ? null : birthdate.toLocalDate(),
                    rs.getString("biography"),
                    rs.getString("city"),
                    rs.getString("password"),
                    gender == null ? null : Gender.valueOf(gender)
            );
        }

    }

}
