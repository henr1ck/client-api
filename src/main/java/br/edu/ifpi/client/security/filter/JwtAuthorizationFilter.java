package br.edu.ifpi.client.security.filter;

import br.edu.ifpi.client.error.exception.InvalidHeaderException;
import br.edu.ifpi.client.error.exception.details.ProblemDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${jwt.algorithm.secret}")
    private String JWT_ALGORITHM_SECRET;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        final String uri = request.getRequestURI();
        return uri.equals("/signin") || uri.equals("/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.contains("Bearer ")) {
                throw new InvalidHeaderException("Failed to extract Authorization Bearer header! " +
                        "Expected format: [Authorization: Bearer <token>]");
            }
            String token = authorizationHeader.substring("Bearer".length()).trim();

            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(JWT_ALGORITHM_SECRET))
                    .withClaim("Access token only", true).build();

            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String subject = decodedJWT.getSubject();
            List<SimpleGrantedAuthority> simpleGrantedAuthorities = decodedJWT.getClaim("authorities").asList(SimpleGrantedAuthority.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(subject, null, simpleGrantedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

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
        }
    }

}
