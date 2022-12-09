package br.edu.ifpi.client.security.config;

import br.edu.ifpi.client.security.filter.JwtAuthenticationFilter;
import br.edu.ifpi.client.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManager authenticationManager;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
                .csrf(CsrfConfigurer::disable)
                .authenticationManager(authenticationManager)
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, JwtAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> {
                    authorize.antMatchers("/signin", "/refresh-token").permitAll();
                    authorize.antMatchers("/api/**").hasAnyRole("USER");
                    authorize.antMatchers("**/admin/**").hasAnyRole("ADMIN");
                    authorize.anyRequest().denyAll();
                });

        return security.build();
    }

//    @Bean
//    public InMemoryUserDetailsManager inMemoryUserDetailsManager(BCryptPasswordEncoder bCryptPasswordEncoder){
//        UserDetails userDetails = User.builder()
//                .username("pedro")
//                .password(bCryptPasswordEncoder.encode("12345"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(userDetails);
//    }

}
