package eu.domibus.connector.client.rest.controller;

import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping(LargeFileController.PUBLISH_URL)
public class LargeFileController {

    public static final String PUBLISH_URL = "/api/files/";

    @Autowired
    LargeFileStorageService largeFileStorageService;


    @RequestMapping("/{ref}")
    public ResponseEntity<InputStreamResource> getLargeFile(@PathVariable("ref") String id) {
        LargeFileStorageService.LargeFileReferenceId referenceId = new LargeFileStorageService.LargeFileReferenceId();
        referenceId.setStorageIdReference(id);

        LargeFileStorageService.LargeFileReference largeFileReference = largeFileStorageService.getLargeFileReference(referenceId).get();

        InputStream inStream = largeFileStorageService.getInputStream(largeFileReference);

        InputStreamResource inputStreamResource = new InputStreamResource(inStream);

        HttpHeaders headers = new HttpHeaders();
        String mimeType = largeFileReference.getContentType();
        if (mimeType == null) {
            mimeType = MediaType.TEXT_PLAIN_VALUE;
        }
//        headers.setContentType();
//        headers.setContentLength(largeFileReference.getContentLength());


        return ResponseEntity.ok()
                .contentLength(largeFileReference.getContentLength())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(inputStreamResource);
    }


}
