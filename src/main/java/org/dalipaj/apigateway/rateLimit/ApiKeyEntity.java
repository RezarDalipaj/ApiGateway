package org.dalipaj.apigateway.rateLimit;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_keys", uniqueConstraints = {
        @UniqueConstraint(columnNames = "lookup_key")
})
public class ApiKeyEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @Column(name = "lookup_key", nullable = false)
    private String lookupKey;

    @OneToOne(mappedBy = "apiKey", cascade = CascadeType.ALL)
    private RateLimitEntity rateLimit;
}
