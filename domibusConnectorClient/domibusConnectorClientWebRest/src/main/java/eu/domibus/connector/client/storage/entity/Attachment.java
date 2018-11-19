package eu.domibus.connector.client.storage.entity;

import javax.persistence.*;

@Entity
@Table(name = "ATTACHMENT")
public class Attachment {

    @TableGenerator(name = "seqStoreAttachment", table = "HIBERNATE_SEQ_TABLE", pkColumnName = "SEQ_NAME", pkColumnValue = "ATTACHMENT.ID", valueColumnName = "SEQ_VALUE", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqStoreAttachment")
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATA_REFERENCE")
    String dataReference; //uri to the data

    @Column(name = "DOCUMENT_NAME")
    String documentName;

    @Column(name = "MIME_TYPE")
    String mimeType;

    @Column(name = "CHECKSUM")
    String checksum;

    @Column(name = "KEY")
    String key;

    @Column(name = "DESCRIPTION")
    String description;

    @OneToOne(optional = true, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "attachment")
    private DetachedSignature detachedSignature;

    @PreUpdate
    public void preUpdate() {
        if (this.detachedSignature != null) {
            this.detachedSignature.setAttachment(this);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.detachedSignature != null) {
            this.detachedSignature.setAttachment(this);
        }
    }

    private String identifier;

    public String getDataReference() {
        return dataReference;
    }

    public void setDataReference(String dataReference) {
        this.dataReference = dataReference;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetachedSignature getDetachedSignature() {
        return detachedSignature;
    }

    public void setDetachedSignature(DetachedSignature detachedSignature) {
        this.detachedSignature = detachedSignature;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
