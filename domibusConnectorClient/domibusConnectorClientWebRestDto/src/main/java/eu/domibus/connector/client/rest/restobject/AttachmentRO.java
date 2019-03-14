package eu.domibus.connector.client.rest.restobject;

public class AttachmentRO {

    String url; //uri to the data

    String storageReference;

    String documentName;

    String mimeType;

    String checksum;

    String key;

    DetachedSignatureRO detachedSignatureRO;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public DetachedSignatureRO getDetachedSignatureRO() {
        return detachedSignatureRO;
    }

    public void setDetachedSignatureRO(DetachedSignatureRO detachedSignatureRO) {
        this.detachedSignatureRO = detachedSignatureRO;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(String storageReference) {
        this.storageReference = storageReference;
    }
}
