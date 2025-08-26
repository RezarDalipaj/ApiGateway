package org.dalipaj.apigateway.route;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.LoadBalancerType;
import org.dalipaj.apigateway.route.backend.BackendEntity;
import org.dalipaj.apigateway.route.oauth.OAuthEntity;
import org.dalipaj.apigateway.user.UserEntity;

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
    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "strip_prefix")
    private boolean stripPrefix = true;

    @Column(name = "auth_type")
    @Enumerated(EnumType.STRING)
    private RouteAuthType authType;

    // load balancing
    @Column(name = "load_balancer_type")
    private LoadBalancerType loadBalancerType;

    @ManyToMany(mappedBy = "routes", fetch = FetchType.EAGER)
    private List<BackendEntity> backends;

    // oauth to upstream (optional)
    @ManyToOne(fetch = FetchType.EAGER)
    private OAuthEntity oauth; // nullable

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;
}

