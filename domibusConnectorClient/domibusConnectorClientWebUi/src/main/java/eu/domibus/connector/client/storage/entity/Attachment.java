package eu.domibus.connector.client.storage.entity;

import eu.domibus.connector.client.rest.dto.DetachedSignatureDTO;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URL;

@Entity
public class Attachment {

    @GeneratedValue
    @Id
    private Long id;

    String dataReference; //uri to the data

    String documentName;

    String mimeType;

    String checksum;

    String key;

    String description;

    @Embedded
    DetachedSignature detachedSignature;

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
