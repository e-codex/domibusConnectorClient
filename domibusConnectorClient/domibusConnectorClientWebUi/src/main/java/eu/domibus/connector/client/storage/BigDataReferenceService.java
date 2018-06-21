package eu.domibus.connector.client.storage;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BigDataReferenceService {


    public BigDataReference getBigDataReference(String key);


    /**
     * create a new BigDataReference, ready to write to
     * @return BigDataReference
     */
    public BigDataReference createBigDataReference();


    /**
     * create a new BigDataReference, ready to write to
     * uses the provided storageIdReference
     * @return BigDataReference
     */
    public BigDataReference createBigDataReference(BigDataReference reference);

    public void deleteBigDataReference(BigDataReference reference);


    public static class BigDataReference implements DataSource {
        @Nullable
        private String storageIdReference = "";
        @Nullable
        private String contentType = "";
        @Nullable
        private String name = "";
        @Nullable
        private OutputStream outputStream;
        @Nullable
        private InputStream inputStream;

        @Nullable
        public String getStorageIdReference() {
            return storageIdReference;
        }

        public void setStorageIdReference(@Nullable String storageIdReference) {
            this.storageIdReference = storageIdReference;
        }

        public void setContentType(@Nullable String contentType) {
            this.contentType = contentType;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        public void setOutputStream(@Nullable OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void setInputStream(@Nullable InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return this.outputStream;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
