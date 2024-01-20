package landau.leo.high.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("post")
public class PostEntity {

    @Id
    @Indexed //for faster retrieval,
    private UUID id;

    private UUID userId;

    private String text;

}
