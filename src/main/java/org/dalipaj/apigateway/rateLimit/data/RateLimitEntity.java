package org.dalipaj.apigateway.rateLimit.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.user.data.UserEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_limits")
public class RateLimitEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "api_key_id", unique = true, nullable = false)
    private ApiKeyEntity apiKey;

    @Column(name = "per_minute", nullable = false)
    private Integer perMinute;

    @Column(name = "per_hour", nullable = false)
    private Integer perHour;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity application;
}