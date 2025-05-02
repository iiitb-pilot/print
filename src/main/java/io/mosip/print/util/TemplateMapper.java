package io.mosip.print.util;

import io.mosip.print.constant.ProcessType;
import io.mosip.print.constant.TemplateType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class TemplateMapper {
    private static final Map<ProcessType, TemplateType> PROCESS_TO_TEMPLATE_TYPE;

    static {
        Map<ProcessType, TemplateType> map = new EnumMap<>(ProcessType.class);
        map.put(ProcessType.NEW, TemplateType.UIN_CARD_TEMPLATE);
        map.put(ProcessType.UPDATE, TemplateType.UIN_CARD_TEMPLATE);
        map.put(ProcessType.CRVS_NEW, TemplateType.UIN_CARD_EMAIL_SUB);

        PROCESS_TO_TEMPLATE_TYPE = Collections.unmodifiableMap(map);
    }

    private TemplateMapper() {
        // Prevent instantiation
    }

    /**
     * Determines the appropriate template type based on the process attributes
     *
     * @param attributes Map of process attributes
     * @return Optional containing the corresponding TemplateType, or empty if no matching process type is found
     */
    public static Optional<TemplateType> determineTemplateType(Map<String, Object> attributes) {
        if (attributes == null || !attributes.containsKey("processType")) {
            return Optional.empty();
        }

        try {
            String processTypeStr = String.valueOf(attributes.get("processType")).toUpperCase();
            ProcessType processType = ProcessType.valueOf(processTypeStr);
            return Optional.ofNullable(PROCESS_TO_TEMPLATE_TYPE.get(processType));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}