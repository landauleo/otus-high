package landau.leo.high.service;

import java.time.ZonedDateTime;
import java.util.UUID;

import landau.leo.high.dao.PostAuditDao;
import landau.leo.high.dao.PostDao;
import landau.leo.high.dto.PostRequest;
import landau.leo.high.entity.PostAuditEntity;
import landau.leo.high.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;
    private final PostAuditDao postAuditDao;
    private final WebSocketService webSocketService;

    public UUID createPost(PostRequest postRequest) {
        UUID postId = UUID.randomUUID();
        UUID postAuditId = UUID.randomUUID();
        PostEntity postEntity = new PostEntity(postId, postRequest.getUserId(), postRequest.getText());
        postDao.insertPost(postEntity);

        //компенсируемые транзакции
        try {
            postAuditDao.insertPostAudit(new PostAuditEntity(postAuditId, postEntity.getUserId(), ZonedDateTime.now()));
            webSocketService.notifyPostCreated(postEntity);
        } catch (Exception e) {
            rollbackPostCreation(postId, postAuditId);
            throw new RuntimeException("Не удалось создать пост", e);
        }

        return postId;
    }

    //компенсационные транзакции
    private void rollbackPostCreation(UUID postId, UUID postAuditId) {
        postDao.deletePost(postId);
        postAuditDao.deletePostAudit(postAuditId);
    }

}
