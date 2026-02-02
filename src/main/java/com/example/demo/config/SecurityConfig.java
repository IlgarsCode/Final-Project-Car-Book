package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // ✅ 4-cü məsələ: provider-i sistemə bağla (sabit işləsin)
                .authenticationProvider(authenticationProvider(passwordEncoder()))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/fonts/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(
                                "/", "/index",
                                "/about", "/services", "/contact",
                                "/pricing", "/pricing/**",
                                "/car", "/car/**",
                                "/blog", "/blog/**"
                        ).permitAll()

                        .requestMatchers("/dashboard/**").hasRole("ADMIN")

                        .requestMatchers(
                                "/cart/**",
                                "/booking/**",
                                "/checkout/**",
                                "/order/**",
                                "/profile/**"
                        ).authenticated()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")

                        // bunu sonra "previous page"-ə qayıtmaq kimi edərik
                        .defaultSuccessUrl("/", true)

                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
