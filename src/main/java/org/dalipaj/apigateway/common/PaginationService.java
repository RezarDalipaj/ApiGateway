package org.dalipaj.apigateway.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public abstract class PaginationService {

    protected <T, ID> Page<T> getAll(Integer pageNumber,
                              Integer pageSize,
                              List<FilterDto> filters,
                              PaginationRepository<T, ID> repository) {
        var pageable = PageRequest.of(pageNumber, pageSize);

        FilterUtil<T> routeFilterUtil = new FilterUtil<>();
        List<Specification<T>> allSpecs = routeFilterUtil.getAllSpecs(filters);
        Specification<T> specification = Specification.allOf(allSpecs);

        return repository.findAll(specification, pageable);
    }
}
