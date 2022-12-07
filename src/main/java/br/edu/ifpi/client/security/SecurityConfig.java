package br.edu.ifpi.client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();

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
