package br.edu.ifpi.client.error.handler;

import br.edu.ifpi.client.error.exception.ClientNotFoundException;
import br.edu.ifpi.client.error.exception.details.BadRequestExceptionDetails;
import br.edu.ifpi.client.error.exception.details.ClientNotFoundExceptionDetails;
import br.edu.ifpi.client.error.exception.details.MethodArgumentNotValidExceptionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResourceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ClientNotFoundException.class)
    public ResponseEntity<Object> clientNotFoundExceptionHandle(ClientNotFoundException exception){
        ClientNotFoundExceptionDetails clientNotFoundExceptionDetails = ClientNotFoundExceptionDetails.builder()
                .message(exception.getLocalizedMessage())
                .exception(exception.getClass().getSimpleName())
                .timestamp(LocalDateTime.now())
                .statusCode(404)
                .build();

        return ResponseEntity.status(clientNotFoundExceptionDetails.getStatusCode())
                .body(clientNotFoundExceptionDetails);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String fields = ex.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining("; "));

        String fieldsMessage = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        MethodArgumentNotValidExceptionDetails exceptionDetails = MethodArgumentNotValidExceptionDetails.builder()
                .message("Validation error")
                .exception(ex.getClass().getSimpleName())
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .fields(fields)
                .fieldsErrorMessage(fieldsMessage)
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(exceptionDetails);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BadRequestExceptionDetails badRequestExceptionDetails = BadRequestExceptionDetails.builder()
                .message("Required request body is missing")
                .exception(ex.getClass().getSimpleName())
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(badRequestExceptionDetails);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BadRequestExceptionDetails exceptionDetails = BadRequestExceptionDetails.builder()
                .message(ex.getLocalizedMessage())
                .exception(ex.getClass().getSimpleName())
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(exceptionDetails);
    }
}
