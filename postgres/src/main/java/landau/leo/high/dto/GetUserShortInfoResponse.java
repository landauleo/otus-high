package landau.leo.high.dto;

import java.util.UUID;

import landau.leo.high.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserShortInfoResponse {

    private UUID id;
    private String firstName;
    private String secondName;

    public static GetUserShortInfoResponse toDto(UserEntity entity) {
        GetUserShortInfoResponse dto = new GetUserShortInfoResponse();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setSecondName(entity.getSecondName());
        return dto;
    }

}
