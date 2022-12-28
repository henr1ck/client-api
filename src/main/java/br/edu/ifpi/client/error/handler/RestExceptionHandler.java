package br.edu.ifpi.client.error.handler;

import br.edu.ifpi.client.error.exception.ResourceNotFoundException;
import br.edu.ifpi.client.error.exception.details.Problem;
import br.edu.ifpi.client.error.exception.details.ProblemDetails;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> clientNotFoundExceptionHandle(ResourceNotFoundException exception, ServletWebRequest request){
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(404)
                .title("Resource not found")
                .detail(exception.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(problemDetails.getStatus())
                .body(problemDetails);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String fieldsMessage = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ProblemDetails details = ProblemDetails.builder()
                .status(status.value())
                .title("Validation error")
                .detail(fieldsMessage)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).headers(headers).body(details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemDetails details = ProblemDetails.builder()
                .status(status.value())
                .title(status.getReasonPhrase())
                .detail(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof DateTimeParseException){
            return handleDateTimeParseException((DateTimeParseException) rootCause, headers, status, request);

        }else if(rootCause instanceof UnrecognizedPropertyException){
            return handleUnrecognizedPropertyException((UnrecognizedPropertyException) rootCause, headers, status, request);

        }else if (rootCause instanceof JsonParseException){
            return handleJsonParseException((JsonParseException) rootCause, headers, status, request);

        }else {
            ProblemDetails problemDetails = ProblemDetails.builder()
                    .status(status.value())
                    .title(Problem.INVALID_REQUEST_BODY)
                    .detail("A JSON Syntax error. Please, check all input properties for this endpoint")
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(status)
                    .headers(headers)
                    .body(problemDetails);
        }
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(status.value())
                .title(status.getReasonPhrase())
                .detail(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(problemDetails);
    }
    public ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(status.value())
                .title(Problem.INVALID_REQUEST_BODY)
                .detail("The date cannot be parsed. Please, check ISO-8601 syntax: YYYY-MM-DDThh:mm:ss")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(problemDetails);
    };

    public ResponseEntity<Object> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        String propertyName = ex.getPropertyName();
        String propertiesAccepted = ex.getKnownPropertyIds().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(status.value())
                .title(Problem.INVALID_REQUEST_BODY)
                .detail(String.format("Unknown property cannot be parsed: '%s'. " +
                        "The following properties are supported for this resource: %s", propertyName, propertiesAccepted))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(problemDetails);
    };

    public ResponseEntity<Object> handleJsonParseException(JsonParseException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(status.value())
                .title(Problem.INVALID_REQUEST_BODY)
                .detail(ex.getOriginalMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(problemDetails);
    };
}
