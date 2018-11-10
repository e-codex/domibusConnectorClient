package eu.domibus.connector.client.rest.dto;

public class ConfirmationDTO {

    byte[] confirmationXml;

    ConfirmationType confirmationType;

    public byte[] getConfirmationXml() {
        return confirmationXml;
    }

    public void setConfirmationXml(byte[] confirmationXml) {
        this.confirmationXml = confirmationXml;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public static enum ConfirmationType {

        SUBMISSION_ACCEPTANCE,
        SUBMISSION_REJECTION,
        RELAY_REMMD_ACCEPTANCE,
        RELAY_REMMD_REJECTION,
        RELAY_REMMD_FAILURE,
        DELIVERY,
        NON_DELIVERY,
        RETRIEVAL,
        NON_RETRIEVAL;

        public String value() {
            return name();
        }

        public static ConfirmationType fromValue(String v) {
            return valueOf(v);
        }

    }


}
