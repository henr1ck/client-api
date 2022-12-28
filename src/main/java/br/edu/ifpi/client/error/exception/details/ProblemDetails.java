package br.edu.ifpi.client.error.exception.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {
    private Integer status;
    private String type;
    private String instance;
    private String title;
    private String detail;
    private LocalDateTime timestamp;
}
