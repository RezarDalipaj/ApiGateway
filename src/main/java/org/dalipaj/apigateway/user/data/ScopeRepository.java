package org.dalipaj.apigateway.user.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ScopeRepository extends JpaRepository<ScopeEntity, Long> {

    List<ScopeEntity> findByNameIn(Collection<String> names);
}
