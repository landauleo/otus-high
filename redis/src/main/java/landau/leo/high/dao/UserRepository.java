package landau.leo.high.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import landau.leo.high.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

@Deprecated
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

    @Override
    Optional<UserEntity> findById(UUID id);

    List<UserEntity> findAllByFirstNameAndSecondName(String firstName, String secondName);

}
