package eu.domibus.connector.client.rest.restobject;

import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;

import javax.xml.bind.annotation.XmlEnumValue;

public class DetachedSignatureRO {

    private byte[] detachedSignature;

    private String signatureName;

    private SignatureType getSignatureMimeType;

    public byte[] getDetachedSignature() {
        return detachedSignature;
    }

    public void setDetachedSignature(byte[] detachedSignature) {
        this.detachedSignature = detachedSignature;
    }

    public String getSignatureName() {
        return signatureName;
    }

    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    public SignatureType getGetSignatureMimeType() {
        return getSignatureMimeType;
    }

    public void setGetSignatureMimeType(SignatureType getSignatureMimeType) {
        this.getSignatureMimeType = getSignatureMimeType;
    }

    public static enum SignatureType {
        BINARY("BINARY"),
        XML("XML"),
        PKCS7("PKCS7");

        private final String value;

        SignatureType(String v) {
            value = v;
        }

        public static SignatureType fromValue(String v) {
            for (SignatureType c: SignatureType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

    }

}



