package br.edu.ifpi.client.error.exception.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class BadRequestExceptionDetails {
    private String message;
    private String exception;
    private LocalDateTime timestamp;
    private Integer statusCode;
}
