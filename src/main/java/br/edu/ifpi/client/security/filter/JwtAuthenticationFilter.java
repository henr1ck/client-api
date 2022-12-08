package br.edu.ifpi.client.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.algorithm.secret}")
    private String JWT_ALGORITHM_SECRET;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationManager authenticationManager = getAuthenticationManager();

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();

        List<String> authorityList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (1 * 60 * 1000)))
                .withSubject(userDetails.getUsername())
                .withClaim("authorities", authorityList)
                .withIssuer(request.getRequestURI())
                .withIssuedAt(Instant.now())
                .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

        String refreshToken = JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + (5 * 60 * 1000)))
                .withSubject(userDetails.getUsername())
                .withIssuer(request.getRequestURI())
                .withIssuedAt(Instant.now())
                .sign(Algorithm.HMAC256(JWT_ALGORITHM_SECRET));

        response.addHeader("Access-Token", accessToken);
        response.addHeader("Refresh-Token", refreshToken);
    }
}
