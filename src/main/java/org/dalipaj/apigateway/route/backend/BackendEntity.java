package org.dalipaj.apigateway.route.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.route.RouteEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "backends", uniqueConstraints = {
        @UniqueConstraint(columnNames = "url")
})
public class BackendEntity {

    @Id
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "weight")
    private Integer weight;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<RouteEntity> routes;
}

