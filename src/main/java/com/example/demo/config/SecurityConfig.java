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

                        // STATIC
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/fonts/**",
                                "/css/**", "/js/**",
                                "/images/**", "/img/**",
                                "/scss/**", "/lib/**", "/plugins/**",
                                "/webjars/**",
                                "/uploads/**"
                        ).permitAll()

                        //  Error page-lər
                        .requestMatchers("/error", "/error/**").permitAll()

                        // AUTH (AZ route-lar)
                        .requestMatchers(
                                "/giris",
                                "/qeydiyyat",
                                "/qeydiyyat-tesdiq",
                                "/sifreni-unutdum",
                                "/otp-tesdiq",
                                "/sifre-yenile"
                        ).permitAll()

                        // Public pages (AZ route-lar)
                        .requestMatchers(
                                "/", "/index",
                                "/haqqimizda",
                                "/xidmetler",
                                "/elaqe",
                                "/qiymetler", "/qiymetler/**",
                                "/masinlar", "/masinlar/**",
                                "/masin", "/masin/**",          // rəy post-u kimi route-lar üçün
                                "/bloq", "/bloq/**",
                                "/reyler"
                        ).permitAll()

                        // SUPER ADMIN
                        .requestMatchers("/dashboard/users/**").hasRole("SUPER_ADMIN")

                        // ADMIN PANEL
                        .requestMatchers("/dashboard/**").hasRole("ADMIN")

                        // USER pages (AZ route-lar)
                        .requestMatchers(
                                "/bloq/yeni",
                                "/menim-bloqlarim",
                                "/menim-bloqlarim/**",
                                "/bloq/*/serh",
                                "/bloq/*/cavab",
                                "/profil/**",

                                "/sebet/**",
                                "/rezerv/**",
                                "/sifaris",           // checkout page route
                                "/payment/**",        // hələ /payment saxlamısan, istəsən /odenis edək (aşağıda izah var)
                                "/sifarislerim/**"    // order controller səndə /sifarislerim idi
                        ).hasRole("USER")

                        .anyRequest().authenticated()
                )

                // LOGIN: /giris
                .formLogin(form -> form
                        .loginPage("/giris")
                        .loginProcessingUrl("/giris")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/giris?xeta=1")
                        .permitAll()
                )

                // 403 handling
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // LOGOUT: /cixis
                .logout(logout -> logout
                        .logoutUrl("/cixis")
                        .logoutSuccessUrl("/giris?cixis=1")
                        .permitAll()
                );

        return http.build();
    }
}