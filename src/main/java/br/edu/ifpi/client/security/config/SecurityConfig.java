package br.edu.ifpi.client.security.config;

import br.edu.ifpi.client.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf().disable()
                .authenticationManager(authenticationManager)
                .addFilter(jwtAuthenticationFilter)
                .authorizeHttpRequests()
                .anyRequest().authenticated();

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
