package eu.domibus.connector.client.rest.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler 
extends ResponseEntityExceptionHandler 
{

	//other exception handlers

	@ExceptionHandler(MessageNotFoundException.class)
	protected ResponseEntity<Object> handleMessageNotFound(
			MessageNotFoundException ex) {
		return buildResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<Object> buildResponseEntity(String apiError, HttpStatus status) {
		return new ResponseEntity<>(apiError, status);
	}
	

}

