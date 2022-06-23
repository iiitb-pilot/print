package io.mosip.print.repository;

import io.mosip.print.entity.PrintTransactionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class PrintSpecificationBuilder {
    private final List<SearchCriteria> params;

    public PrintSpecificationBuilder() {
        params = new ArrayList<>();
    }

    public PrintSpecificationBuilder with(boolean isOrPredicate, String key, SearchOperation operation, Object value) {
        params.add(new SearchCriteria(key, operation, value, isOrPredicate));
        return this;
    }

    public Specification<PrintTransactionEntity> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(PrintSearchSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(specs.get(i))
                    : Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}
