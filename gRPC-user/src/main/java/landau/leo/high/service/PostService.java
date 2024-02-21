package landau.leo.high.service;

import java.util.UUID;

import landau.leo.high.dao.PostDao;
import landau.leo.high.dto.PostRequest;
import landau.leo.high.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;

    public UUID createPost(PostRequest postRequest) {
        UUID id = UUID.randomUUID();
        PostEntity postEntity = new PostEntity(id, postRequest.getUserId(), postRequest.getText());
        postDao.insertPost(postEntity);

        return id;
    }

}
