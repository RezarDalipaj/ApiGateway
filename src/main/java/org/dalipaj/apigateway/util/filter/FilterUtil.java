package org.dalipaj.apigateway.util.filter;

import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.model.dto.FilterDto;
import org.dalipaj.apigateway.model.dto.KeyValue;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class FilterUtil<T> {

    public Specification<T> filterFieldWithEqualOperator(KeyValue keyValue) {
        return (root, query, builder) -> builder.equal(root.get(keyValue.getKey()), keyValue.getValue());
    }

    public Specification<T> filterFieldWithLikeOperator(KeyValue keyValue) {
        return (root, query, builder) -> builder.like(root.get(keyValue.getKey()), keyValue.getValue().toString());
    }

    public Specification<T> filterWithAndEqualOperators(FilterDto filterDto) {
        var allSpecs = filterDto.getInternalKeyValues().stream()
                .map(this::filterFieldWithEqualOperator)
                .toList();

        return Specification.allOf(allSpecs);
    }

    public Specification<T> filterWithAndLikeOperators(FilterDto filterDto) {
        var allSpecs = filterDto.getInternalKeyValues().stream()
                .map(this::filterFieldWithLikeOperator)
                .toList();

        return Specification.allOf(allSpecs);
    }

    public Specification<T> filterWithOrEqualOperators(FilterDto filterDto) {
        var allSpecs = filterDto.getInternalKeyValues().stream()
                .map(this::filterFieldWithEqualOperator)
                .toList();

        return Specification.anyOf(allSpecs);
    }
}
