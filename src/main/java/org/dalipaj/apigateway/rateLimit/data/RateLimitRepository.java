package org.dalipaj.apigateway.rateLimit.data;

import org.dalipaj.apigateway.common.pagination.PaginationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends PaginationRepository<RateLimitEntity, Long> {
}
