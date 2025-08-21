package org.dalipaj.apigateway.repository;

import lombok.NonNull;
import org.dalipaj.apigateway.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String email);
    boolean existsByUsername(String email);
    boolean existsByEmail(String email);
    void deleteById(@NonNull Long id);
}
