package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // PUBLIC
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()

                // EMPLOYES (directeur + administratif)
                .requestMatchers("/employes/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF")

                // RESIDENTS
                // Lecture pour soignant + éducateur
                .requestMatchers(HttpMethod.GET, "/residents/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF", "SOIGNANT", "EDUCATEUR")

                // Création / modification réservées à directeur + administratif
                .requestMatchers(HttpMethod.POST, "/residents/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF")

                .requestMatchers(HttpMethod.PUT, "/residents/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF")

                // FAMILLES
                .requestMatchers("/familles/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF", "SOIGNANT", "EDUCATEUR")

                // DOSSIER MEDICAL + CONSULTATIONS + TRAITEMENTS
                .requestMatchers("/dossiers/**", "/consultations/**", "/traitements/**")
                    .hasAnyRole("DIRECTEUR", "SOIGNANT")

                // ACTIVITES
                .requestMatchers("/activites/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF", "EDUCATEUR")

                // PLANNING
                .requestMatchers("/plannings/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF", "SOIGNANT", "EDUCATEUR")

                // FACTURES + PAIEMENTS (comptable + directeur)
                .requestMatchers("/factures/**", "/paiements/**")
                    .hasAnyRole("DIRECTEUR", "FINANCE")

                // EQUIPEMENTS
                .requestMatchers("/equipements/**")
                    .hasAnyRole("DIRECTEUR", "ADMINISTRATIF")

                // TOUT LE RESTE
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard", true)
                    .permitAll()
            )

            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))

            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
