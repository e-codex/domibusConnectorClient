package eu.domibus.connector.client.storage.service;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public interface LargeFileStorageService {


    public Optional<LargeFileReference> getLargeFileReference(LargeFileReferenceId key);


    /**
     * create a new LargeFileReference, ready to write to
     * @return LargeFileReference
     */
    public LargeFileReference createLargeFileReference();


    public void deleteLargeFileReference(LargeFileReference reference);

    //TODO: read only exception, locked exception, ioException?
    //
    public OutputStream getOutputStream(LargeFileReference reference);

    public InputStream getInputStream(LargeFileReference reference);


    public static class LargeFileReference {

        @Nullable
        private LargeFileReferenceId storageIdReference;

        @Nullable
        public LargeFileReferenceId getStorageIdReference() {
            return storageIdReference;
        }

        private String contentType;

        private long contentLength;

        public void setStorageIdReference(@Nullable LargeFileReferenceId storageIdReference) {
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
    }

    public static class LargeFileReferenceId {
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
