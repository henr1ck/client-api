package br.edu.ifpi.client.error.exception.details;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class MethodArgumentNotValidExceptionDetails extends BadRequestExceptionDetails{
    private String fields;
    private String fieldsErrorMessage;
}
