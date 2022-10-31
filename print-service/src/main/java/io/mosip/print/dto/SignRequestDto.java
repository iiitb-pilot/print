package io.mosip.print.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignRequestDto {
	@NotBlank
	private String data;
}
