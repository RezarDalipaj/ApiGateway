package org.dalipaj.apigateway.route.data.oauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthRepository extends JpaRepository<OAuthEntity, Long> {

    Optional<OAuthEntity> findByName(String name);
}
