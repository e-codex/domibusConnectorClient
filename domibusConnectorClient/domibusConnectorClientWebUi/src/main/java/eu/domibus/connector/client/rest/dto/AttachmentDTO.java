package eu.domibus.connector.client.rest.dto;

import java.net.URL;

public class AttachmentDTO {

    URL url; //uri to the data

    String documentName;

    String mimeType;

    String checksum;

    String key;

    DetachedSignatureDTO detachedSignatureDTO;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
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

    public DetachedSignatureDTO getDetachedSignatureDTO() {
        return detachedSignatureDTO;
    }

    public void setDetachedSignatureDTO(DetachedSignatureDTO detachedSignatureDTO) {
        this.detachedSignatureDTO = detachedSignatureDTO;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
