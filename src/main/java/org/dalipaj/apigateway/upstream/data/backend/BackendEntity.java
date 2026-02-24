package org.dalipaj.apigateway.upstream.data.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.route.data.RouteEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "backends", uniqueConstraints = {
        @UniqueConstraint(columnNames = "host")
})
public class BackendEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "health_check_path")
    private String healthCheckPath;

    @Column(name = "weight")
    private Integer weight;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "route_backends",
            joinColumns = @JoinColumn(name = "backend_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "route_id", nullable = false)
    )
    private List<RouteEntity> routes = new ArrayList<>();
}

