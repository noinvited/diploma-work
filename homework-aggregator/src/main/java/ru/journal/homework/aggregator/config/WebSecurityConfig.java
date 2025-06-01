package ru.journal.homework.aggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.domain.helperEntity.Role;
import ru.journal.homework.aggregator.domain.helperEntity.Status;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "registration", "/activate/*", "/afterRegistration").permitAll()
                        .requestMatchers("/login").anonymous()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            User user = (User) authentication.getPrincipal();
                            if (user.getRole().equals(Role.ADMIN)) {
                                response.sendRedirect("/");
                            } else if (user.getStatus() != null && user.getStatus() == Status.TEACHER) {
                                response.sendRedirect("/teacherSchedule");
                            } else if (user.getStatus() != null && user.getStatus() == Status.STUDENT) {
                                response.sendRedirect("/studentSchedule");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                )
                .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer.key("1234"))
                .exceptionHandling(configurer ->
                        configurer.accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                                    if (user.getStatus() != null && user.getStatus() == Status.TEACHER) {
                                        response.sendRedirect("/teacherSchedule");
                                    } else if (user.getStatus() != null && user.getStatus() == Status.STUDENT) {
                                        response.sendRedirect("/studentSchedule");
                                    } else {
                                        response.sendRedirect("/");
                                    }
                                }))
                .logout(LogoutConfigurer::permitAll)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

