package landau.leo.high.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {

    private UUID id;

    private UUID userId;

    private String text;

}
