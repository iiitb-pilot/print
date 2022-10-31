package io.mosip.print.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintResponse extends BaseRestResponseDTO {

	private static final long serialVersionUID = 1L;

	private List<ErrorDTO> errors;

    private ResponseDTO response;
}
