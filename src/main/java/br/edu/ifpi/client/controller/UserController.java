package br.edu.ifpi.client.controller;

import br.edu.ifpi.client.error.exception.InvalidHeaderException;
import br.edu.ifpi.client.error.exception.details.BadRequestExceptionDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/")
@RequiredArgsConstructor
public class UserController {
    @Value("${jwt.algorithm.secret}")
    private String JWT_ALGORITHM_SECRET;

    private final UserDetailsService userDetailsService;

    @SneakyThrows
    @PostMapping(path = "/refresh-token")
    public ResponseEntity<Object> refreshtoken(HttpServletRequest request){
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.contains("Bearer ")) {
                throw new InvalidHeaderException("Failed to extract Authorization Bearer header! " +
                        "Expected format: [Authorization: Bearer <token>]");
            }
            String token = authorizationHeader.substring("Bearer".length()).trim();

            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(JWT_ALGORITHM_SECRET))
                    .withClaim("Refresh token only", true)
                    .build();

            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String subject = decodedJWT.getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            String accessToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                    .withSubject(userDetails.getUsername())
                    .withClaim("Access token only", true)
                    .withClaim("authorities", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .withIssuer(request.getRequestURI())
                    .withIssuedAt(Instant.now())
                    .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

            String refreshToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                    .withSubject(userDetails.getUsername())
                    .withClaim("Refresh token only", true)
                    .withIssuer(request.getRequestURI())
                    .withIssuedAt(Instant.now())
                    .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Token", accessToken);
            headers.add("Refresh-Token", refreshToken);

            return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
        } catch (RuntimeException exception) {
            BadRequestExceptionDetails badRequestExceptionDetails = BadRequestExceptionDetails.builder()
                    .exception(exception.getClass().getSimpleName())
                    .message(exception.getLocalizedMessage())
                    .timestamp(LocalDateTime.now())
                    .statusCode(400)
                    .build();

            return ResponseEntity.status(badRequestExceptionDetails.getStatusCode()).body(badRequestExceptionDetails);
        }
    }
}
