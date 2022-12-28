package br.edu.ifpi.client.security.filter;

import br.edu.ifpi.client.error.exception.InvalidHeaderException;
import br.edu.ifpi.client.error.exception.details.ProblemDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.algorithm.secret}")
    private String JWT_ALGORITHM_SECRET;

    private ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/signin");
    }

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        try {
            if (username == null || password == null){
                throw new InvalidHeaderException("Username header or Password header is invalid for sign-in");
            }
        } catch (RuntimeException exception) {
            ProblemDetails problemDetails = ProblemDetails.builder()
                    .status(400)
                    .title("Header validation")
                    .detail(exception.getLocalizedMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            String problemDetailsAsJson = objectMapper.writeValueAsString(problemDetails);
            response.setStatus(problemDetails.getStatus());
            response.getWriter().print(problemDetailsAsJson);

            return null;
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationManager authenticationManager = getAuthenticationManager();

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();

        List<String> authorityList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withSubject(userDetails.getUsername())
                .withClaim("authorities", authorityList)
                .withClaim("Access token only", true)
                .withIssuer(request.getRequestURI())
                .withIssuedAt(Instant.now())
                .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

        String refreshToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .withSubject(userDetails.getUsername())
                .withClaim("Refresh token only", true)
                .withIssuer(request.getRequestURI())
                .withIssuedAt(Instant.now())
                .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

        response.addHeader("Access-Token", accessToken);
        response.addHeader("Refresh-Token", refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(401)
                .title("Authentication failed")
                .detail(failed.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        String problemDetailsAsJson = objectMapper.writeValueAsString(problemDetails);
        response.setStatus(problemDetails.getStatus());
        response.getWriter().print(problemDetailsAsJson);
    }
}
