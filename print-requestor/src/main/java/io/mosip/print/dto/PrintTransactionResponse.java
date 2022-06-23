package io.mosip.print.dto;

import io.mosip.print.entity.PrintTransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PrintTransactionResponse implements Serializable {
    private String printId;
    private String credentialTransactionId;
    private String regId;
    private String statusCode;
    private String statusComment;
    private String langCode;
    private LocalDateTime printDate;

    public PrintTransactionResponse(PrintTransactionEntity entity) {
        new PrintTransactionResponse(entity.getPrintId(), entity.getCredentialTransactionId(), entity.getRegId(),
                entity.getStatusCode(), entity.getStatusComment(), entity.getLangCode(), entity.getPrintDate());
    }
}
