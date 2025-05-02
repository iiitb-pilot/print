package io.mosip.print.constant;

public enum TemplateType {
    UIN_CARD_TEMPLATE("RPR_UIN_CARD_TEMPLATE"),
    UIN_CARD_EMAIL_SUB("RPR_UIN_CARD_EMAIL_SUB"),
    UIN_CARD_EMAIL("RPR_UIN_CARD_EMAIL");

    private final String value;

    TemplateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() { return value; }
}