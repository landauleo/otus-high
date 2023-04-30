package landau.leo.high.dto;

import java.time.LocalDate;
import java.util.UUID;

import landau.leo.high.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {

    private UUID id;
    private String firstName;
    private String secondName;
    private LocalDate birthdate;
    private String biography;
    private String city;
    private Gender gender;

    public static GetUserResponse toDto(UserEntity entity) {
        GetUserResponse dto = new GetUserResponse();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setSecondName(entity.getSecondName());
        dto.setBirthdate(entity.getBirthdate());
        dto.setBiography(entity.getBiography());
        dto.setCity(entity.getCity());
        dto.setGender(entity.getGender());
        return dto;
    }

}
