package io.mosip.print.repository;

public enum SearchOperation {
    EQUALITY, NEGATION, GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO, LIKE,
    STARTS_WITH, ENDS_WITH, CONTAINS, IN;
}
