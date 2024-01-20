package landau.leo.high.service;

import landau.leo.high.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private SimpMessagingTemplate template;

    public void notifyPostCreated(PostEntity post) {
        template.convertAndSend("/topic/posted", post);
    }

}

