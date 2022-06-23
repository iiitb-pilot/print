package io.mosip.print.controller;

import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.print.core.http.ResponseWrapper;
import io.mosip.print.dto.PrintSearchRequestDto;
import io.mosip.print.dto.PrintTransactionResponse;
import io.mosip.print.logger.PrintLogger;
import io.mosip.print.model.EventModel;
import io.mosip.print.service.PrintRequestService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/print-requestor")
public class PrintRequestController {

	/** The printservice. */
	@Autowired
	private PrintRequestService printService;
	
	@Value("${mosip.event.topic}")
	private String topic;

	private static Logger printLogger = PrintLogger.getLogger(PrintRequestController.class);


	/**
	 *  Gets the file.
	 *
	 * @param eventModel
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/callback/notifyPrint", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthenticateContentAndVerifyIntent(secret = "${mosip.event.secret}", callback = "/v1/print-requestor/callback/notifyPrint", topic = "${mosip.event.topic}")
	public ResponseEntity<String> handleSubscribeEvent(@RequestBody EventModel eventModel) throws Exception {
		printLogger.info("Print request event received from websub, id: {}", eventModel.getEvent().getId());
		boolean isPrintRequestSuccess = printService.createRequest(eventModel);
		printLogger.info("printing status : {} for event id: {}", isPrintRequestSuccess,eventModel.getEvent().getId());
		return new ResponseEntity<>("request accepted.", HttpStatus.OK);
	}

	@PostMapping (path = "/printList", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("PrintListener")
	public ResponseWrapper<List<PrintTransactionResponse>> getPrintList(@RequestBody PrintSearchRequestDto printSearchRequestDto) throws Exception {
		printLogger.info("Print list request received with criteria :{}", printSearchRequestDto);
		ResponseWrapper<List<PrintTransactionResponse>> response = new ResponseWrapper<>();
		List<PrintTransactionResponse> printResponses = printService.getPrintRecords(printSearchRequestDto);
		response.setResponse(printResponses);
		printLogger.info("Number of records fetched: {}", (printResponses != null) ? printResponses.size() : 0);
		return response;
	}

	@PostMapping (path = "/printOrReprint", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("PrintListener")
	public void printOrReprint(@RequestBody List<String> registrationIds) throws Exception {
		printLogger.info("Print or Reprint request received for RIDs :{}", registrationIds);
		printService.printOrReprint(registrationIds);
		printLogger.info("Credential request created for RIDs :{}", registrationIds);
	}
}
