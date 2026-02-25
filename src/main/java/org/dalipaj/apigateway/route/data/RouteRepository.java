package org.dalipaj.apigateway.route.data;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {

    Optional<RouteEntity> findByPath(String path);

    @EntityGraph(attributePaths = { "targets" })
    @NonNull
    Optional<RouteEntity> findById(@NonNull Long id);

    @EntityGraph(attributePaths = { "targets" })
    @NonNull
    List<RouteEntity> findAll();
}
