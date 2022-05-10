package io.mosip.print.util;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.print.core.http.ResponseWrapper;
import io.mosip.print.dto.NotificationResponseDTO;
import io.mosip.print.logger.PrintLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Component
public class NotificationUtil {

    Logger log = PrintLogger.getLogger(NotificationUtil.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${emailResourse.url}")
    private String emailResourseUrl;

    @Autowired
    private TemplateGenerator templateGenerator;

    @Value("${mosip.utc-datetime-pattern}")
    private String dateTimeFormat;

    public NotificationResponseDTO emailNotification(String emailId, String fileName, byte[] attachmentFile) throws IOException {
        log.info("sessionId", "idType", "id", "In emailNotification method of NotificationUtil service");
        HttpEntity<byte[]> doc = null;
        String fileText = null;
        if (attachmentFile != null) {
            LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
            pdfHeaderMap.add("Content-disposition",
                    "form-data; name=attachments; filename=" + fileName);
            pdfHeaderMap.add("Content-type", "application/pdf");
            doc = new HttpEntity<>(attachmentFile, pdfHeaderMap);
        }

        ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = null;
        String mergeTemplate = null;
//        for (KeyValuePairDto keyValuePair : acknowledgementDTO.getFullName()) {
//            if (acknowledgementDTO.getIsBatch()) {
//                fileText = templateUtil.getTemplate(keyValuePair.getKey(), cancelAppoinment);
//            } else {
//                fileText = templateUtil.getTemplate(keyValuePair.getKey(), emailAcknowledgement);
//            }
//
//            String languageWiseTemplate = templateUtil.templateMerge(fileText, acknowledgementDTO);
//            if (mergeTemplate == null) {
//                mergeTemplate = languageWiseTemplate;
//            } else {
//                mergeTemplate += System.lineSeparator() + languageWiseTemplate;
//            }
//        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
        emailMap.add("attachments", doc);
        emailMap.add("mailContent", getEmailContent());
        emailMap.add("mailSubject", getEmailSubject());
        emailMap.add("mailTo", emailId);
        HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
        log.info("sessionId", "idType", "id",
                "In emailNotification method of NotificationUtil service emailResourseUrl: " + emailResourseUrl);
        try {
            resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity,
                    new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {
                    });
        } catch (RestClientException e) {
            log.error("Error while sending pdf email.", e);
        }
        NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
        if (resp != null) {
            notifierResponse.setMessage(resp.getBody().getResponse().getMessage());
            notifierResponse.setStatus(resp.getBody().getResponse().getStatus());
        }
        return notifierResponse;
    }

    private String getEmailContent() {
        return "UIN attached in PDF form.";
    }

    private String getEmailSubject() throws IOException {
        return "UIN Attached";
    }

    private String getCurrentResponseTime() {
        log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of NotificationUtil service");
        return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
    }
}
