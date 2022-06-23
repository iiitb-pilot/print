package io.mosip.print.service;

import io.mosip.print.dto.PrintSearchRequestDto;
import io.mosip.print.dto.PrintTransactionResponse;
import io.mosip.print.model.EventModel;

import java.util.List;

public interface PrintRequestService {

	Boolean createRequest(EventModel eventModel) throws Exception;

	List<PrintTransactionResponse> getPrintRecords(PrintSearchRequestDto printSearchRequestDto);

	void printOrReprint(List<String> registrationIds);

}