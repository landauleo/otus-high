package landau.leo.high.entity;

import java.time.LocalDate;
import java.util.UUID;

import landau.leo.high.dto.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user")
public class UserEntity {

    @Id
    @Indexed //for faster retrieval,
    private UUID id;
    private String firstName;
    private String secondName;
    private LocalDate birthdate;
    private String biography;
    private String city;
    private String password;
    private Gender gender;

    public UserEntity(UUID id, String firstName, String secondName) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
    }

}
