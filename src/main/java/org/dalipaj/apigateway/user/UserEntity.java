package org.dalipaj.apigateway.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.rateLimit.RateLimitEntity;
import org.dalipaj.apigateway.route.RouteEntity;

import java.util.List;

@Entity
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "email"})
})
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RateLimitEntity> rateLimits;

    @OneToMany(fetch = FetchType.LAZY)
    private List<RouteEntity> routes;
}
