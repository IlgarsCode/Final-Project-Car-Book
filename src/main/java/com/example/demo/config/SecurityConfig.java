package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;

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

    // SUPER_ADMIN > ADMIN > USER
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            ROLE_SUPER_ADMIN > ROLE_ADMIN
            ROLE_ADMIN > ROLE_USER
        """);
        return hierarchy;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider(passwordEncoder()))
                .authorizeHttpRequests(auth -> auth

                        // ✅ STATIC
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/fonts/**",
                                "/css/**", "/js/**",
                                "/images/**", "/img/**",
                                "/scss/**", "/lib/**", "/plugins/**",
                                "/webjars/**",
                                "/uploads/**",
                                "/admin/**"
                        ).permitAll()

                        // ✅ Error page-lər hamıya açıq olmalıdır
                        .requestMatchers("/error", "/error/**").permitAll()

                        // ✅ Auth pages
                        .requestMatchers("/auth/**").permitAll()

                        // ✅ Public pages
                        .requestMatchers(
                                "/", "/index",
                                "/about", "/services", "/contact",
                                "/pricing", "/pricing/**",
                                "/car", "/car/**",
                                "/blog", "/blog/**",
                                "/testimonial"
                        ).permitAll()

                        // ✅ SUPER ADMIN (spesifik)
                        .requestMatchers("/dashboard/users/**").hasRole("SUPER_ADMIN")

                        // ✅ ADMIN PANEL
                        .requestMatchers("/dashboard/**").hasRole("ADMIN")

                        // ✅ USER pages
                        .requestMatchers(
                                "/blog/new",
                                "/my-blogs",
                                "/my-blogs/**",
                                "/blog/*/comment",
                                "/blog/*/reply",
                                "/profile/**",
                                "/cart/**",
                                "/booking/**",
                                "/checkout/**",
                                "/payment/**",
                                "/order/**",
                                "/testimonial/new"
                        ).hasRole("USER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                // ✅ 403 handling düzgün: status 403 saxlayır, view 403 açır
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}