package br.edu.ifpi.client.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestBody {
    @NotBlank(message = "First name cannot be null or empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be null or empty")
    private String lastName;
    @NotBlank(message = "Phone number cannot be null or empty")
    private String phoneNumber;
    @NotBlank(message = "Email cannot be null or empty")
    private String email;
    @NotNull(message = "Birth date cannot be null") @Past(message = "Birth date cannot be in the future")
    private LocalDateTime birthDate;
}
