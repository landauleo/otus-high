package landau.leo.high.dao;

import java.util.List;
import java.util.UUID;

import landau.leo.high.entity.DialogMessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface DialogMessageRepository extends CrudRepository<DialogMessageEntity, UUID> {

    List<DialogMessageEntity> findAllByFrom(UUID fromUserId);

}
