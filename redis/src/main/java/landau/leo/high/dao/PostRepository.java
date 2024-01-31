package landau.leo.high.dao;

import java.util.List;
import java.util.UUID;

import landau.leo.high.entity.PostEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Deprecated
public interface PostRepository extends CrudRepository<PostEntity, UUID> {

    @Query(value = "SELECT *  FROM post LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PostEntity> findAllWithOffSetAndLimit(@Param("facilityId") int offset, @Param("facilityId") long limit);

}
