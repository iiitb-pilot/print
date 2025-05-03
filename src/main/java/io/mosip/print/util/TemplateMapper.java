package io.mosip.print.util;

import io.mosip.print.constant.ProcessType;
import io.mosip.print.constant.TemplateType;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class TemplateMapper {
    private static final Map<ProcessType, TemplateMappedConfig> PROCESS_TO_TEMPLATE_TYPE;

    static {
        Map<ProcessType, TemplateMappedConfig> map = new EnumMap<>(ProcessType.class);
        map.put(ProcessType.NEW, new TemplateMappedConfig(
                TemplateType.UIN_CARD_TEMPLATE,
                TemplateType.UIN_CARD_EMAIL_SUB,
                TemplateType.UIN_CARD_EMAIL
        ));

        PROCESS_TO_TEMPLATE_TYPE = Collections.unmodifiableMap(map);
    }

    private TemplateMapper() {
        // Prevent instantiation
    }

    /**
     * Retrieves the template configuration mapped to the specified process type
     * from the provided attributes map. If the map does not contain a valid
     * process type or is invalid, an empty {@code Optional} is returned.
     *
     * @param attributes a map of attributes where the key "processType" is
     *                   expected to represent the process type as a string.
     * @return an {@code Optional} containing the {@code TemplateMappedConfig}
     *         corresponding to the process type if available; otherwise, an
     *         empty {@code Optional}.
     */
    public static Optional<TemplateMappedConfig> getTemplatesConfig(Map<String, Object> attributes) {
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

    @Getter
    public static class TemplateMappedConfig {
        private final TemplateType documentTemplateName;
        private final TemplateType emailSubjectTemplate;
        private final TemplateType emailTemplate;

        public TemplateMappedConfig(TemplateType documentTemplateName, TemplateType emailSubjectTemplate, TemplateType emailTemplate) {
            this.documentTemplateName = documentTemplateName;
            this.emailSubjectTemplate = emailSubjectTemplate;
            this.emailTemplate = emailTemplate;
        }

    }
}