package org.dalipaj.apigateway.application.data;

import lombok.NonNull;
import org.dalipaj.apigateway.common.pagination.PaginationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends PaginationRepository<ApplicationEntity, Long> {

    ApplicationEntity findByName(String application);

    boolean existsByName(String application);

    void deleteById(@NonNull Long id);
}
