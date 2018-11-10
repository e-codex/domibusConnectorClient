package eu.domibus.connector.client.storage.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable
public class DetachedSignature {

    private byte[] detachedSignature;

    private String signatureName;

    private SignatureType signatureMimeType;

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

    public SignatureType getSignatureMimeType() {
        return signatureMimeType;
    }

    public void setSignatureMimeType(SignatureType getSignatureMimeType) {
        this.signatureMimeType = getSignatureMimeType;
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



