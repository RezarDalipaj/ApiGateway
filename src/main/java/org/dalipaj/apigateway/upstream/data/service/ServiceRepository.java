package org.dalipaj.apigateway.upstream.data.service;

import lombok.NonNull;
import org.dalipaj.apigateway.common.pagination.PaginationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends PaginationRepository<ServiceEntity, Long> {

    @EntityGraph(attributePaths = { "routes", "routes.backends", "application.name" })
    @NonNull
    Optional<ServiceEntity> findById(@NonNull Long serviceId);

    @EntityGraph(attributePaths = { "routes", "routes.backends" })
    @NonNull
    Page<ServiceEntity> findAll(Specification<ServiceEntity> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = { "routes", "routes.backends" })
    @NonNull
    List<ServiceEntity> findAll();
}
