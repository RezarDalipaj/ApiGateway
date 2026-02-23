package org.dalipaj.apigateway.upstream.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BackendRepository extends JpaRepository<BackendEntity, Long> {

    Optional<BackendEntity> findByHost(String host);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO route_backends (route_id, backend_id)
        SELECT :routeId, :backendId
        WHERE NOT EXISTS (
            SELECT 1
            FROM route_backends rb
            WHERE rb.route_id = :routeId
              AND rb.backend_id = :backendId
        )
        """, nativeQuery = true)
    void addRouteToBackendIfMissing(@Param("routeId") Long routeId,
                                    @Param("backendId") Long backendId);
}
