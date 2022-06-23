package io.mosip.print.service.impl;

import io.mosip.print.activemq.PrintMQListener;
import io.mosip.print.constant.ApiName;
import io.mosip.print.constant.LoggerFileConstant;
import io.mosip.print.constant.PrintTransactionStatus;
import io.mosip.print.constant.QrVersion;
import io.mosip.print.core.http.RequestWrapper;
import io.mosip.print.core.http.ResponseWrapper;
import io.mosip.print.dto.*;
import io.mosip.print.entity.PrintTransactionEntity;
import io.mosip.print.exception.*;
import io.mosip.print.logger.PrintLogger;
import io.mosip.print.model.EventModel;
import io.mosip.print.repository.PrintSearchSpecification;
import io.mosip.print.repository.PrintSpecificationBuilder;
import io.mosip.print.repository.PrintTransactionRepository;
import io.mosip.print.service.PrintRequestService;
import io.mosip.print.service.PrintRestClientService;
import io.mosip.print.service.UinCardGenerator;
import io.mosip.print.spi.CbeffUtil;
import io.mosip.print.spi.QrCodeGenerator;
import io.mosip.print.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrintRequestServiceImpl implements PrintRequestService {

	private String topic = "CREDENTIAL_STATUS_UPDATE";
	private Random sr = null;
	private static final int max = 999999;
	private static final int min = 100000;

	@Autowired
	private WebSubSubscriptionHelper webSubSubscriptionHelper;

	@Autowired
	private DataShareUtil dataShareUtil;

	@Autowired
	CryptoUtil cryptoUtil;

	@Autowired
	private RestApiClient restApiClient;

	@Autowired
	private PrintRestClientService<Object> printRestClientService;

	@Autowired
	private CryptoCoreUtil cryptoCoreUtil;

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = File.separator;

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant UIN_CARD_TEMPLATE. */
	private static final String UIN_CARD_TEMPLATE = "RPR_UIN_CARD_TEMPLATE";

	/** The Constant FACE. */
	private static final String FACE = "Face";

	/** The Constant UIN_TEXT_FILE. */
	private static final String UIN_TEXT_FILE = "textFile";

	/** The Constant APPLICANT_PHOTO. */
	private static final String APPLICANT_PHOTO = "ApplicantPhoto";

	/** The Constant QRCODE. */
	private static final String QRCODE = "QrCode";

	/** The Constant UINCARDPASSWORD. */
	private static final String UINCARDPASSWORD = "mosip.registration.processor.print.service.uincard.password";

	/** The print logger. */
	Logger printLogger = PrintLogger.getLogger(PrintRequestServiceImpl.class);

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The template generator. */
	@Autowired
	private TemplateGenerator templateGenerator;

	/** The utilities. */
	@Autowired
	private Utilities utilities;

	/** The uin card generator. */
	@Autowired
	private UinCardGenerator<byte[]> uinCardGenerator;

	/** The qr code generator. */
	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;


	/** The Constant VID_CREATE_ID. */
	public static final String VID_CREATE_ID = "registration.processor.id.repo.generate";

	/** The Constant REG_PROC_APPLICATION_VERSION. */
	public static final String REG_PROC_APPLICATION_VERSION = "registration.processor.id.repo.vidVersion";

	/** The Constant DATETIME_PATTERN. */
	public static final String DATETIME_PATTERN = "mosip.print.datetime.pattern";

	public static final String VID_TYPE = "registration.processor.id.repo.vidType";

	/** The cbeffutil. */
	@Autowired
	private CbeffUtil cbeffutil;

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private CredentialsVerifier credentialsVerifier;

	@Value("${mosip.datashare.partner.id}")
	private String partnerId;

	@Value("${mosip.datashare.policy.id}")
	private String policyId;

	@Value("${mosip.template-language}")
	private String templateLang;

	@Value("#{'${mosip.mandatory-languages:}'.concat('${mosip.optional-languages:}')}")
	private String supportedLang;

	@Value("${mosip.print.verify.credentials.flag:true}")
	private boolean verifyCredentialsFlag;

	@Value("${mosip.datashare.cardprint.partner.id}")
	private String cardPrintPartnerId;

	@Value("${mosip.datashare.cardprint.policy.id}")
	private String cardPrintPolicyId;

	@Value("${token.request.clientId}")
	private String clientId;

	@Value("${mosip.send.uin.email.attachment.enabled:false}")
	private Boolean emailUINEnabled;

	@Value("${mosip.registration.processor.encrypt:false}")
	private boolean encrypt;

	@Autowired
	private PrintMQListener activePrintMQListener;

	@Autowired
	@Qualifier("printTransactionRepository")
	PrintTransactionRepository printTransactionRepository;

	@Autowired
	private NotificationUtil notificationUtil;

	@Override
	public Boolean createRequest(EventModel eventModel) {
		String credential = null;
		Boolean isPrinted = Boolean.FALSE;
		try {
			if (eventModel.getEvent().getDataShareUri() == null || eventModel.getEvent().getDataShareUri().isEmpty()) {
				credential = eventModel.getEvent().getData().get("credential").toString();
			} else {
				String dataShareUrl = eventModel.getEvent().getDataShareUri();
				URI dataShareUri = URI.create(dataShareUrl);
				credential = restApiClient.getApi(dataShareUri, String.class);
			}

		if (eventModel.getEvent().getData().get("registrationId") == null) {
			printLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), "RID" +
							PlatformErrorMessages.PRT_RID_MISSING_EXCEPTION.name());
			throw new IdentityNotFoundException(
					PlatformErrorMessages.PRT_RID_MISSING_EXCEPTION.getMessage());
		}


		String decodedCredential = cryptoCoreUtil.decrypt(credential);
		if (verifyCredentialsFlag) {
			printLogger.info("Configured received credentials to be verified. Flag {}", verifyCredentialsFlag);
			boolean verified = credentialsVerifier.verifyCredentials(decodedCredential);
			if (!verified) {
				printLogger.error("Received Credentials failed in verifiable credential verify method. So, the credentials will not be printed." +
					" Id: {}, Transaction Id: {}", eventModel.getEvent().getId(), eventModel.getEvent().getTransactionId());
				return false;
			}
		}
		storePrintDetails(credential, decodedCredential, eventModel);
//
//			byte[] pdfbytes = getDocuments(decodedCredential,
//					eventModel.getEvent().getData().get("credentialType").toString(), ecryptionPin,
//					eventModel.getEvent().getTransactionId(), "UIN", false, eventModel.getEvent().getId(),
//					eventModel.getEvent().getData().get("registrationId").toString(), eventModel.getEvent().getData().get("vid").toString()).get("uinPdf");
			isPrinted = Boolean.TRUE;
		} catch (Exception e){
			printLogger.error(e.getMessage() , e);
			isPrinted = Boolean.FALSE;
		}
		return isPrinted;
	}

	@Override
	public List<PrintTransactionResponse> getPrintRecords(PrintSearchRequestDto printSearchRequestDto) {
		PrintSpecificationBuilder builder = new PrintSpecificationBuilder();
		List<PrintSearchSpecification> specificationList = new ArrayList<>();
		if (printSearchRequestDto.getCriteriaList() != null ) {
			printSearchRequestDto.getCriteriaList().forEach(
					searchCriteria -> {
						builder.with(false, searchCriteria.getKey(), searchCriteria.getOperation(), searchCriteria.getValue());
					}
			);
		}
		Specification<PrintTransactionEntity> printSearchSpec = builder.build();
		Pageable pageable = PageRequest.of(printSearchRequestDto.getPageNumber(), printSearchRequestDto.getPageSize());
		Page<PrintTransactionEntity> entityPage = printTransactionRepository.findAll(printSearchSpec, pageable);
		if (entityPage != null && entityPage.hasContent()) {
			return entityPage.getContent().stream().map(PrintTransactionResponse::new).collect(Collectors.toList());
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public void printOrReprint(List<String> registrationIds) {
		List<RequestWrapper<CredentialRequestDto>> requestWrapperList = new ArrayList<>();
		List<PrintTransactionEntity> printRecordList = printTransactionRepository.findAllByIsDeletedAndRegIdIn(false, registrationIds);

		printRecordList.forEach (
				printRecord -> {
					//prepare credential request for all print records.
					requestWrapperList.add(createCredentialRequest(printRecord));
				}
		);
		// call credential service to persist print request.
		requestWrapperList.forEach (
			requestWrapper -> {
				try {
					ResponseWrapper<?> responseWrapper = (ResponseWrapper<?>) printRestClientService.postApi(ApiName.CREDENTIALREQUEST, null, null,
							requestWrapper, ResponseWrapper.class, MediaType.APPLICATION_JSON);
				} catch (Exception e) {
					//update print transaction table with status comment for particular RID
					String regId = (String) requestWrapper.getRequest().getAdditionalData().get("registrationId");
					printLogger.error("Print credential request creation failed for RID: {}",
							regId, e);
					String errorMessage = ExceptionUtils.buildMessage("credential request creation failed.", e);
					printTransactionRepository.updateStatusComment(errorMessage, regId);
				}
			}
		);
	}

	private RequestWrapper<CredentialRequestDto> createCredentialRequest(PrintTransactionEntity printRecord) {
		RequestWrapper<CredentialRequestDto> requestWrapper = new RequestWrapper<>();
		String encryptedData = printRecord.getPrintData();//decrypt and decode
		RequestWrapper<SignatureRequestDto> cryptoRequestWrapper = new RequestWrapper<>();
		cryptoRequestWrapper.setRequest(getSignatureRequest(encryptedData));
		String vid = "", credentialType = "";
		try {
			ResponseWrapper<SignatureResponseDto> response = (ResponseWrapper<SignatureResponseDto>) printRestClientService.postApi(ApiName.CRYPTOMANAGERDECRYPT, null, null,
					cryptoRequestWrapper, ResponseWrapper.class, MediaType.APPLICATION_JSON);
			String decryptedData = response.getResponse().getData();
			String credentialJson = new String(java.util.Base64.getDecoder().decode(decryptedData));
			JSONObject credentialJsonObj = JsonUtil.objectMapperReadValue(credentialJson, JSONObject.class);
			vid = (String) credentialJsonObj.get("vid");
			credentialType = (String) credentialJsonObj.get("credentialType");
		} catch (ApisResourceAccessException e) {
			printLogger.error(e.getMessage());
		} catch (IOException ex) {
			printLogger.error(ex.getMessage());
		}

		//parse "credentialJson" to get VID and RID, CredentialType
		CredentialRequestDto credentialRequestDto = new CredentialRequestDto();
		credentialRequestDto.setAdditionalData(new LinkedHashMap<>());
		credentialRequestDto.getAdditionalData().put("registrationId", printRecord.getRegId());
		credentialRequestDto.getAdditionalData().put("vid", vid);
		credentialRequestDto.setCredentialType(credentialType);
		requestWrapper.setId(env.getProperty("mosip.registration.processor.credential.request.service.id"));
		requestWrapper.setRequest(credentialRequestDto);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
		LocalDateTime localdatetime = LocalDateTime
				.parse(io.mosip.kernel.core.util.DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
		requestWrapper.setRequesttime(localdatetime);
		requestWrapper.setVersion("1.0");
		return requestWrapper;
	}

	private CredentialRequestDto getCredentialRequestDto(String regId) {
		CredentialRequestDto credentialRequestDto = new CredentialRequestDto();

		credentialRequestDto.setCredentialType(env.getProperty("mosip.registration.processor.credentialtype"));
		credentialRequestDto.setEncrypt(encrypt);

		credentialRequestDto.setId(regId);

		credentialRequestDto.setIssuer(env.getProperty("mosip.registration.processor.issuer"));

		credentialRequestDto.setEncryptionKey(generatePin());

		return credentialRequestDto;
	}

	private void storePrintDetails(String credential, String decodedCredential, EventModel eventModel) {
		String encryptionPin = eventModel.getEvent().getData().get("protectionKey").toString();

		try {
			String credentialSubject = getCredentialSubject(decodedCredential);
			org.json.JSONObject credentialSubjectJson = new org.json.JSONObject(credentialSubject);
			org.json.JSONObject decryptedJson = decryptAttribute(credentialSubjectJson, encryptionPin, decodedCredential);
			PrintTransactionEntity entity = getPrintTransactionEntity(credential, decryptedJson, eventModel);
			printTransactionRepository.save(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private PrintTransactionEntity getPrintTransactionEntity(String credential, org.json.JSONObject decryptedJson, EventModel eventModel) {
		boolean isError = false;
		String statusComment = "";
		String rid = (String) eventModel.getEvent().getData().get("registrationId");
		String transactionId = eventModel.getEvent().getTransactionId();
		PrintTransactionEntity entity = preparePrintEntity(rid, transactionId);
		try {
			RequestWrapper<SignatureRequestDto> cryptoRequestWrapper = new RequestWrapper<>();
			String encodedPrintData = getEncodedPrintData(eventModel, rid);
			cryptoRequestWrapper.setRequest(getSignatureRequest(encodedPrintData));
			ResponseWrapper<SignatureResponseDto> response = (ResponseWrapper<SignatureResponseDto>) printRestClientService.postApi(ApiName.CRYPTOMANAGERENCRYPT, null, null,
					cryptoRequestWrapper, ResponseWrapper.class, MediaType.APPLICATION_JSON);
			String encryptedPrintData = (response.getResponse() != null ) ? response.getResponse().getData() : null;
			entity.setPrintData(encryptedPrintData);
		} catch (IOException e) {
			isError = true;
			statusComment = e.getMessage();
		} catch (ApisResourceAccessException e) {
			isError = true;
			statusComment = e.getErrorCode() + ";" + e.getErrorText() + ";" + e.getMessage();
		} finally {
			if (isError) {
				entity.setStatusCode(PrintTransactionStatus.ERROR.name());
			}
			entity.setStatusComment(statusComment);
		}
		return entity;
	}

	private String getEncodedPrintData(EventModel eventModel, String rid) throws IOException {
		Map<String, Object> printDataMap = new HashMap<>();
		printDataMap.put("rid", rid);
		printDataMap.put("vid", (String) eventModel.getEvent().getData().get("vid"));
		printDataMap.put("credentialId", (String) eventModel.getEvent().getData().get("credentialId"));
		printDataMap.put("credentialType", (String) eventModel.getEvent().getData().get("credentialType"));
		printDataMap.put("printLocation", (String) eventModel.getEvent().getData().get("printLocation"));
		String printJsonData = JsonUtil.writeValueAsString(printDataMap);
		return java.util.Base64.getEncoder().encodeToString(printJsonData.getBytes());
	}

	private SignatureRequestDto getSignatureRequest(String data) {
		return new SignatureRequestDto(env.getProperty("mosip.application.id"),
				env.getProperty("mosip.reference.id"), DateUtils.getUTCCurrentDateTimeString(),
				data);
	}

	private PrintTransactionEntity preparePrintEntity ( String rid, String transactionId) {
		PrintTransactionEntity entity = new PrintTransactionEntity();
		entity.setCredentialTransactionId(transactionId);
		entity.setLangCode("eng");
		entity.setDeleted(Boolean.FALSE);
		entity.setPrintId(UUID.randomUUID().toString());
		entity.setRegId(rid);
		entity.setStatusCode(PrintTransactionStatus.NEW.name());
		entity.setCrBy(env.getProperty("mosip.application.id"));
		entity.setCrDate(DateUtils.getUTCCurrentDateTime());
		return entity;
	}

	private String generatePin() {
		if (sr == null)
			instantiate();
		int randomInteger = sr.nextInt(max - min) + min;
		return String.valueOf(randomInteger);
	}

	private void instantiate() {
		printLogger.debug("Instantiating SecureRandom for credential pin generation............");
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			printLogger.error("Could not instantiate SecureRandom for credential pin generation", e);
		}
	}

	private String getCredentialSubject(String crdential) {
		org.json.JSONObject jsonObject = new org.json.JSONObject(crdential);
		String credentialSubject = jsonObject.get("credentialSubject").toString();
		return credentialSubject;
	}

	public org.json.JSONObject decryptAttribute(org.json.JSONObject data, String encryptionPin, String credential)
			throws ParseException {

		// org.json.JSONObject jsonObj = new org.json.JSONObject(credential);
		JSONParser parser = new JSONParser(); // this needs the "json-simple" library
		Object obj = parser.parse(credential);
		JSONObject jsonObj = (org.json.simple.JSONObject) obj;

		JSONArray jsonArray = (JSONArray) jsonObj.get("protectedAttributes");
		if (Objects.isNull(jsonArray)) {
			return data;
		}
		for (Object str : jsonArray) {

				CryptoWithPinRequestDto cryptoWithPinRequestDto = new CryptoWithPinRequestDto();
				CryptoWithPinResponseDto cryptoWithPinResponseDto = new CryptoWithPinResponseDto();

				cryptoWithPinRequestDto.setUserPin(encryptionPin);
				cryptoWithPinRequestDto.setData(data.getString(str.toString()));
				try {
					cryptoWithPinResponseDto = cryptoUtil.decryptWithPin(cryptoWithPinRequestDto);
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
					printLogger.error("Error while decrypting the data" ,e);
					throw new CryptoManagerException(PlatformErrorMessages.PRT_INVALID_KEY_EXCEPTION.getCode(),
							PlatformErrorMessages.PRT_INVALID_KEY_EXCEPTION.getMessage(), e);
				}
				data.put((String) str, cryptoWithPinResponseDto.getData());

			}

		return data;
	}

}
	
