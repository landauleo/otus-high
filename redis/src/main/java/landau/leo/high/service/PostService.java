package landau.leo.high.service;

import java.util.UUID;

import landau.leo.high.dao.LuaPostRepository;
import landau.leo.high.dto.PostRequest;
import landau.leo.high.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final LuaPostRepository postRepository;
    private final WebSocketService webSocketService;

    public UUID createPost(PostRequest postRequest) throws Exception {
        UUID id = UUID.randomUUID();
        PostEntity postEntity = new PostEntity(id, postRequest.getUserId(), postRequest.getText());
        postRepository.save(postEntity);

        webSocketService.notifyPostCreated(postEntity);
        return id;
    }

}
