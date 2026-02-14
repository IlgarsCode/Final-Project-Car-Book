package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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

                        // ✅ STATIC (login olmadan hər şey görünsün deyə geniş açırıq)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/fonts/**",
                                "/css/**", "/js/**",
                                "/images/**", "/img/**",
                                "/scss/**", "/lib/**", "/plugins/**",
                                "/webjars/**",
                                "/uploads/**",
                                "/admin/**" // admin panel template-lərin assetləri burdadırsa
                        ).permitAll()

                        // ✅ Auth pages
                        .requestMatchers("/auth/**").permitAll()

// ✅ Public pages
                                .requestMatchers(
                                        "/", "/index",
                                        "/about", "/services", "/contact",
                                        "/pricing", "/pricing/**",
                                        "/car", "/car/**",
                                        "/blog", "/blog/**",
                                        "/testimonial"          // ✅ yalnız bu public olsun
                                ).permitAll()

                        // ✅ SUPER ADMIN (spesifik olanı yuxarıda saxla)
                        .requestMatchers("/dashboard/users/**").hasRole("SUPER_ADMIN")

                        // ✅ ADMIN PANEL
                        .requestMatchers("/dashboard/**").hasRole("ADMIN")


                                .requestMatchers(
                                        "/cart/**",
                                        "/booking/**",
                                        "/checkout/**",
                                        "/order/**",
                                        "/profile/**",
                                        "/testimonial/new"                 // ✅ bunu düz yaz
                                ).hasRole("USER")

                        // ✅ Qalan hər şey login istəsin
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
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
