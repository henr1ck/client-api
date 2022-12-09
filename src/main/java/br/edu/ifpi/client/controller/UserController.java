package br.edu.ifpi.client.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
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
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/")
@RequiredArgsConstructor
public class UserController {
    @Value("${jwt.algorithm.secret}")
    private String JWT_ALGORITHM_SECRET;

    private final UserDetailsService userDetailsService;

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<Void> refreshtoken(HttpServletRequest request){
        String token = request.getHeader("Refresh-Token");

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(JWT_ALGORITHM_SECRET)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String subject = decodedJWT.getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

        String accessToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withSubject("userDetails.getUsername()")
                .withClaim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withIssuer(request.getRequestURI())
                .withIssuedAt(Instant.now())
                .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

        String refreshToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .withSubject(userDetails.getUsername())
                .withIssuer(request.getRequestURI())
                .withIssuedAt(Instant.now())
                .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Token", accessToken);
        headers.add("Refresh-Token", refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }
}
