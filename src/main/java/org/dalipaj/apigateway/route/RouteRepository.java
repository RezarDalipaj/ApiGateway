package org.dalipaj.apigateway.route;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, String>, JpaSpecificationExecutor<RouteEntity> {

    @NonNull Page<RouteEntity> findAll(Specification<RouteEntity> specification, @NonNull Pageable pageable);
}
