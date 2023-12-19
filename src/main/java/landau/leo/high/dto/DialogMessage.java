package landau.leo.high.dto;

import java.util.UUID;

import landau.leo.high.entity.DialogMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogMessage {

    private UUID from;
    private UUID to;
    private String text;

    public static DialogMessage toDto(DialogMessageEntity entity) {
        DialogMessage dto = new DialogMessage();
        dto.setFrom(entity.getFrom());
        dto.setTo(entity.getTo());
        dto.setText(entity.getText());
        return dto;
    }

}
