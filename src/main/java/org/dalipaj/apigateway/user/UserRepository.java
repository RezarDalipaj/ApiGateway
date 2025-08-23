package org.dalipaj.apigateway.user;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByUsername(String email);
    boolean existsByUsername(String email);
    boolean existsByEmail(String email);
    void deleteById(@NonNull Long id);
}
