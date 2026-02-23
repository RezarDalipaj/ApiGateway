package org.dalipaj.apigateway.application;

import lombok.NonNull;
import org.dalipaj.apigateway.common.PaginationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends PaginationRepository<ApplicationEntity, Long> {

    ApplicationEntity findByName(String application);

    boolean existsByName(String application);

    void deleteById(@NonNull Long id);
}
