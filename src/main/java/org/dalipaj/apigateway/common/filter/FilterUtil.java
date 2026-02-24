package org.dalipaj.apigateway.common.filter;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
public class FilterUtil<T> {

    private static final String PERCENTAGE = "%";

    private Specification<T> filterField(FilterDto filter) {
        if (filter.getOperator() == FilterOperator.LIKE)
            return (root, query, builder) ->
                    builder.like(getEntityField(root, filter.getKey()), PERCENTAGE.concat(filter.getValue().toString()).concat(PERCENTAGE));

        return (root, query, builder) ->
                builder.equal(getEntityField(root, filter.getKey()), filter.getValue());
    }

    private <Y> Path<Y> getEntityField(Root<T> root, String fullKey) {
        if (!fullKey.contains("."))
            return root.get(fullKey);

        var keys = fullKey.split("\\.");
        Path<Y> field = root.get(keys[0]);

        for (int i = 1; i < keys.length; i++)
            field = field.get(keys[i]);

        return field;
    }

    public List<Specification<T>> getAllSpecs(List<FilterDto> filters) {
        if (isEmpty(filters))
            return Collections.emptyList();

        return filters.stream()
                .map(this::filterField)
                .toList();
    }
}
