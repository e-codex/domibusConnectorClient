package eu.domibus.connector.client.filesystem.configuration.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.domibus.connector.client.filesystem.configuration.DirectoryConfigurationProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FolderWriteableValidator implements ConstraintValidator<CheckFolderWriteable, DirectoryConfigurationProperties> {

    @Override
    public boolean isValid(DirectoryConfigurationProperties dir, ConstraintValidatorContext context) {
        if (dir == null) {
        	String message = String.format("Provided direction properties [%s] is null!", dir);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        
        if(dir.getPath() == null) {
        	String message = String.format("Provided file path [%s] does not exist! Check if the path is correct and exists!", dir.getPath());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        if (Files.notExists(dir.getPath())) {
        	if(dir.isCreateIfNonExistent()) {
        		try {
					Files.createDirectory(dir.getPath());
				} catch (IOException e) {
					 String message = String.format("Provided file path [%s] does not exist! Though the property to create path is enabled this failed as well! Check if the path is correct!", dir.getPath());
			         context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			         return false;
				}
        	}
        }
        
        if (Files.notExists(dir.getPath())) {
            String message = String.format("Provided file path [%s] does not exist! Check if the path is correct and exists!", dir.getPath());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        
        if (!Files.isDirectory(dir.getPath())) {
            String message = String.format("Provided file path [%s] is not a directory! Check the path!", dir.getPath());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (!Files.isWritable(dir.getPath())) {
            String message = String.format("Cannot write to provided path [%s]!", dir.getPath());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
