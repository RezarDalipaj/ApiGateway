package org.dalipaj.apigateway.user.data;

import lombok.NonNull;
import org.dalipaj.apigateway.common.pagination.PaginationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PaginationRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteById(@NonNull Long id);
}

