package io.mosip.print.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PrintResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<PrintTransactionResponse> printResponses;
}
