package org.dalipaj.apigateway.rateLimit;

import org.dalipaj.apigateway.common.PaginationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends PaginationRepository<RateLimitEntity, Long> {
}
