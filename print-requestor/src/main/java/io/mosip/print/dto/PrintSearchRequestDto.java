package io.mosip.print.dto;

import io.mosip.print.constant.IdType;
import io.mosip.print.repository.SearchCriteria;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Instantiates a new print the request.
 */
@Data
public class PrintSearchRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SearchCriteria> criteriaList;

	private int pageNumber;

	private int pageSize;
}
