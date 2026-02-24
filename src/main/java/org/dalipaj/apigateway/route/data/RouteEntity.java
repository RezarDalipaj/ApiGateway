package org.dalipaj.apigateway.route.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerType;
import org.dalipaj.apigateway.upstream.data.backend.BackendEntity;
import org.dalipaj.apigateway.upstream.data.service.ServiceEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "path")
})
public class RouteEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path", nullable = false)
    private String path;

    // load balancing
    @Column(name = "load_balancer_type")
    private LoadBalancerType loadBalancerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "routes")
    private List<BackendEntity> backends = new ArrayList<>();
}

