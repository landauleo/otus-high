package landau.leo.high.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAuditEntity {

    private UUID id;

    private UUID userId;

    private ZonedDateTime createdDate;

}
