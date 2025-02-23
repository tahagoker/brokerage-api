package com.brokerage.stockorder.config;

import com.brokerage.stockorder.security.JwtAuthorizationFilter;
import com.brokerage.stockorder.security.JwtTokenProvider;
import com.brokerage.stockorder.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomerService customerService;

  public SecurityConfig(JwtTokenProvider jwtTokenProvider,
      CustomerService customerService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.customerService = customerService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/api/v{version:\\d+}/auth/**").permitAll()
            .requestMatchers("/api/v{version:\\d+}/auth/register").permitAll()
            .requestMatchers("/api/v{version:\\d+}/auth/login").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class)
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }


  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customerService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}