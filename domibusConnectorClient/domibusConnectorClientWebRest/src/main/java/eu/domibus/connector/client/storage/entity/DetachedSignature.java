package eu.domibus.connector.client.storage.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

@Entity
@Table(name = "DETACHED_SIGNATURE")
public class DetachedSignature {

    @TableGenerator(name = "seqStoreDetachedSignature", table = "HIBERNATE_SEQ_TABLE", pkColumnName = "SEQ_NAME", pkColumnValue = "DETACHED_SIGNATURE.ID", valueColumnName = "SEQ_VALUE", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqStoreDetachedSignature")
    @Id
    Long id;

    @Lob
    @Column(name = "DETACHED_SIGNATURE")
    private byte[] detachedSignature;

    @Column(name = "SIGNATURE_NAME")
    private String signatureName;

    @Column(name = "SIGNATURE_MIME_TYPE")
    private SignatureType signatureMimeType;

    @OneToOne(optional = false)
    @JoinColumn(referencedColumnName = "ID", name = "ATTACHMENT_ID")
    private Attachment attachment;

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

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

        public String getValue() {
            return value;
        }
    }

}



