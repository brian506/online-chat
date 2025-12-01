package org.common.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmTokenRepository extends CrudRepository<FcmToken,String> {
}
