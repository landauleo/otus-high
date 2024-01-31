package landau.leo.high.dto;

import java.util.UUID;

import landau.leo.high.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private UUID id;
    private UUID userId;
    private String text;

    public static PostResponse toDto(PostEntity postEntity) {
        return new PostResponse(postEntity.getId(), postEntity.getUserId(), postEntity.getText());
    }
}
