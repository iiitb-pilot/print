package io.mosip.print.test.util;

import io.mosip.print.constant.TemplateType;
import io.mosip.print.util.TemplateMapper;
import org.junit.Test;
import static org.junit.Assert.*;

public class TemplateMappedConfigTest {

    @Test
    public void testValidConfigWithAllTemplates() {
        TemplateMapper.TemplateMappedConfig config = new TemplateMapper.TemplateMappedConfig(
            TemplateType.UIN_CARD_TEMPLATE,
            TemplateType.UIN_CARD_EMAIL_SUB,
            TemplateType.UIN_CARD_EMAIL
        );

        assertNotNull(config);
        assertEquals(TemplateType.UIN_CARD_TEMPLATE, config.getDocumentTemplateName());
        assertEquals(TemplateType.UIN_CARD_EMAIL_SUB, config.getEmailSubjectTemplate());
        assertEquals(TemplateType.UIN_CARD_EMAIL, config.getEmailTemplate());
    }

    @Test
    public void testValidConfigWithOnlyDocumentTemplate() {
        TemplateMapper.TemplateMappedConfig config = new TemplateMapper.TemplateMappedConfig(
            TemplateType.UIN_CARD_TEMPLATE,
            null,
            null
        );

        assertNotNull(config);
        assertEquals(TemplateType.UIN_CARD_TEMPLATE, config.getDocumentTemplateName());
        assertNull(config.getEmailSubjectTemplate());
        assertNull(config.getEmailTemplate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigWithEmailTemplateButNoSubject() {
        new TemplateMapper.TemplateMappedConfig(
            TemplateType.UIN_CARD_TEMPLATE,
            null,
            TemplateType.UIN_CARD_EMAIL
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigWithEmailSubjectButNoTemplate() {
        new TemplateMapper.TemplateMappedConfig(
            TemplateType.UIN_CARD_TEMPLATE,
            TemplateType.UIN_CARD_EMAIL_SUB,
            null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigWithNoTemplates() {
        new TemplateMapper.TemplateMappedConfig(null, null, null);
    }

    @Test
    public void testValidConfigWithOnlyEmailTemplates() {
        TemplateMapper.TemplateMappedConfig config = new TemplateMapper.TemplateMappedConfig(
            null,
            TemplateType.UIN_CARD_EMAIL_SUB,
            TemplateType.UIN_CARD_EMAIL
        );

        assertNotNull(config);
        assertNull(config.getDocumentTemplateName());
        assertEquals(TemplateType.UIN_CARD_EMAIL_SUB, config.getEmailSubjectTemplate());
        assertEquals(TemplateType.UIN_CARD_EMAIL, config.getEmailTemplate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigWithNullDocumentAndOneEmailComponent() {
        new TemplateMapper.TemplateMappedConfig(
            null,
            TemplateType.UIN_CARD_EMAIL_SUB,
            null
        );
    }
}