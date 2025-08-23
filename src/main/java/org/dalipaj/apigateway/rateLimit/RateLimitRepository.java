package org.dalipaj.apigateway.rateLimit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimitEntity, Long>,
        JpaSpecificationExecutor<RateLimitEntity> {
}
