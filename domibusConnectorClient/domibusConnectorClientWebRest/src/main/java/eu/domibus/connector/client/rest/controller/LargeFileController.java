package eu.domibus.connector.client.rest.controller;

import eu.domibus.connector.client.rest.dto.FileReferenceRO;
import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

@RestController
@RequestMapping(LargeFileController.PUBLISH_URL)
public class LargeFileController {

    private static final Logger LOGGER = LogManager.getLogger(LargeFileController.class);

    public static final String PUBLISH_URL = "/api/files";

    @Autowired
    LargeFileStorageService largeFileStorageService;

    @ApiOperation(value = "Make a DELETE request to delete the file",
            produces = "application/json")
    @ApiResponses({@ApiResponse(code = 404, message = "No File with this id exists")})
    @DeleteMapping("/{ref}")
    public ResponseEntity<String> deleteFile(
            @ApiParam(required = true, value = "The file id")
            @PathVariable("ref") String id) {
        LargeFileStorageService.LargeFileReferenceId referenceId = new LargeFileStorageService.LargeFileReferenceId();
        referenceId.setStorageIdReference(id);

        Optional<LargeFileStorageService.LargeFileReference> largeFileReference = largeFileStorageService.getLargeFileReference(referenceId);
        if (largeFileReference.isPresent()) {
            largeFileStorageService.deleteLargeFileReference(largeFileReference.get());
            return new ResponseEntity<>("deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("not found", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{ref}")
    @ApiOperation(value = "Returns the file, the content type is set in respect to the file type")
    public ResponseEntity<InputStreamResource> getLargeFile(
            @ApiParam(required = true, value = "The file id")
            @PathVariable("ref") String id) {
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

        return ResponseEntity.ok()
                .contentLength(largeFileReference.getContentLength())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(inputStreamResource);
    }


    @PostMapping(value = "/create")
    @ApiOperation(value = "Make a POST request to upload the file",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LargeFileStorageService.LargeFileReference> uploadFileToReference(

            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart("file") MultipartFile file) {

        String contentType = file.getContentType();

        LargeFileStorageService.LargeFileReference largeFileReference = largeFileStorageService.createLargeFileReference();
        largeFileReference.setContentType(contentType);

        try (InputStream inputStream = file.getInputStream();
             OutputStream out = largeFileStorageService.getOutputStream(largeFileReference)) {
            StreamUtils.copy(inputStream, out);
            return new ResponseEntity<>(largeFileReference, HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("IOException occured while uplaoding file", e);
            return new ResponseEntity<>(new LargeFileStorageService.LargeFileReference(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private FileReferenceRO mapFromStorageToRestFileRef(LargeFileStorageService.LargeFileReference largeFileReference) {
        FileReferenceRO returnedFileRef = new FileReferenceRO();
        returnedFileRef.setContentType(largeFileReference.getContentType());
        returnedFileRef.setContentLength(largeFileReference.getContentLength());
        returnedFileRef.setFileReference(largeFileReference.getStorageIdReference().getStorageIdReference());
        return returnedFileRef;
    }



}
