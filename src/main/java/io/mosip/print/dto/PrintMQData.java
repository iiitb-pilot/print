package io.mosip.print.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrintMQData {
    private String id;
    private String refId;
    private String data;
}
