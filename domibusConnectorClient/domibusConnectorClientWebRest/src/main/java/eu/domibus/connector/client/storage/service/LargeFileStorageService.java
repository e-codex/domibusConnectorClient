package eu.domibus.connector.client.storage.service;

import org.springframework.core.style.ToStringCreator;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public interface LargeFileStorageService {


    Optional<LargeFileReference> getLargeFileReference(LargeFileReferenceId key);


    /**
     * create a new LargeFileReference, ready to write to
     * @return LargeFileReference
     */
    LargeFileReference createLargeFileReference();

    LargeFileReference createLargeFileReference(LargeFileReference largeFileReference);

    default void deleteLargeFileReference(LargeFileReferenceId refId) {
        Optional<LargeFileReference> largeFileReference = this.getLargeFileReference(refId);
        this.deleteLargeFileReference(largeFileReference.get());
    }

    void deleteLargeFileReference(LargeFileReference reference);

    OutputStream getOutputStream(LargeFileReference reference);

    InputStream getInputStream(LargeFileReference reference);


    static class LargeFileReference {

        @Nullable
        private LargeFileReferenceId storageIdReference;

        private String name;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String toString() {
            return new ToStringCreator(this)
                    .append("name", name)
                    .append("storageIdReference", storageIdReference)
                    .append("contentType", contentType)
                    .append("contentLength", contentLength)
                    .toString();
        }
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

        public String toString() {
            return new ToStringCreator(this)
                    .append("storageIdReference", storageIdReference)
                    .toString();
        }
    }

}
