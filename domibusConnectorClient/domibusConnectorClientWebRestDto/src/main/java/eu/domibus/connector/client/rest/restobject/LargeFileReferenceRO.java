package eu.domibus.connector.client.rest.restobject;


import io.swagger.annotations.ApiModel;

@ApiModel
public class LargeFileReferenceRO {


    //can be null...
    private LargeFileReferenceId storageIdReference;

    private String name;

    public LargeFileReferenceId getStorageIdReference() {
        return storageIdReference;
    }

    private String contentType;

    private long contentLength;

    public void setStorageIdReference(LargeFileReferenceId storageIdReference) {
        this.storageIdReference = storageIdReference;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    static class LargeFileReferenceId {
        private String storageIdReference = "";

        public LargeFileReferenceId() {}

        public LargeFileReferenceId(String id) {
            this.storageIdReference = id;
        }

        public String getStorageIdReference() {
            return storageIdReference;
        }

        public void setStorageIdReference(String storageIdReference) {
            this.storageIdReference = storageIdReference;
        }
    }

}
