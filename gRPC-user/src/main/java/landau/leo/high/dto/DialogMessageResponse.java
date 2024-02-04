package landau.leo.high.dto;

import java.util.UUID;

import landau.leo.high.generated.DialogueServiceOuterClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogMessageResponse {

    private UUID from;
    private UUID to;
    private String text;

    public static DialogMessageResponse toDto(DialogueServiceOuterClass.DialogMessage response) {
        DialogMessageResponse dto = new DialogMessageResponse();
        dto.setFrom(UUID.fromString(response.getFromUserId()));
        dto.setTo(UUID.fromString(response.getToUserId()));
        dto.setText(response.getText());
        return dto;
    }

}
