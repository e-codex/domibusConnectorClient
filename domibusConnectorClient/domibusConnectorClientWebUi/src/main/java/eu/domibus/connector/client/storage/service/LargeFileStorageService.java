package eu.domibus.connector.client.storage.service;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public interface LargeFileStorageService {


    public Optional<LargeFileReference> getLargeFileReference(String key);


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
        private String storageIdReference = "";

        @Nullable
        public String getStorageIdReference() {
            return storageIdReference;
        }

        public void setStorageIdReference(@Nullable String storageIdReference) {
            this.storageIdReference = storageIdReference;
        }

    }

}
