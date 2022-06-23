package io.mosip.print.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SearchCriteria implements Serializable {
    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;
}
