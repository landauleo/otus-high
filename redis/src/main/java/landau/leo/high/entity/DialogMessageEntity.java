package landau.leo.high.entity;

import java.util.UUID;

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
@RedisHash("dialog_message")
public class DialogMessageEntity {

    @Id
    @Indexed //for faster retrieval,
    private UUID from;
    private UUID to;
    private String text;

}
