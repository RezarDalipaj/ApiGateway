package org.dalipaj.apigateway.upstream.data.target;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TargetRepository extends JpaRepository<TargetEntity, Long> {

    Optional<TargetEntity> findByHost(String host);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO route_targets (route_id, target_id)
        SELECT :routeId, :targetId
        WHERE NOT EXISTS (
            SELECT 1
            FROM route_targets rt
            WHERE rt.route_id = :routeId
              AND rt.target_id = :targetId
        )
        """, nativeQuery = true)
    void addRouteToTargetIfMissing(@Param("routeId") Long routeId,
                                   @Param("targetId") Long targetId);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE rt
        FROM route_targets rt
        WHERE rt.route_id IN (
            SELECT r.id
            FROM routes r
            WHERE r.service_id = :serviceId
        )
        """, nativeQuery = true)
    void deleteTargetRoutesByServiceId(@Param("serviceId") Long serviceId);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE rt
        FROM route_targets rt
        WHERE rt.route_id IN (
            SELECT r.id
            FROM routes r
            JOIN services s ON s.id = r.service_id
            WHERE s.application_id = :appId
        )
        """, nativeQuery = true)
    void deleteTargetRoutesByAppId(@Param("appId") Long appId);
}
