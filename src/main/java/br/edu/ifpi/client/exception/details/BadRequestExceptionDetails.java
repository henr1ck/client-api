package br.edu.ifpi.client.exception.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestExceptionDetails {
    private String message;
    private String exception;
    private LocalDateTime timestamp;
    private Integer statusCode;
}
