package org.dalipaj.apigateway.rateLimit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.user.UserEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_limits", uniqueConstraints = {
        @UniqueConstraint(columnNames = "api_key")
})
public class RateLimitEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "per_minute")
    private Integer perMinute;

    @Column(name = "per_hour")
    private Integer perHour;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;
}