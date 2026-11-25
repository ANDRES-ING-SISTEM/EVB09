package com.citas.app.CitasBack.config;

import org.springframework.beans.factory.annotation.Value;
import com.citas.app.CitasBack.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Sesión
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                // Registro de pacientes (HU-01) y perfil propio (HU-03)
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/*").authenticated()
                // Especialistas (HU-04, HU-20, HU-21)
                .requestMatchers(HttpMethod.POST, "/api/especialistas").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/especialistas", "/api/especialistas/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/especialistas/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_ESPECIALISTA")
                // Especialidades (HU-06, HU-07, HU-08, HU-20)
                .requestMatchers(HttpMethod.GET, "/api/especialidades").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/especialidades/activas", "/api/especialidades/*/detalle").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/especialidades").hasAnyAuthority("ROLE_ESPECIALISTA", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/especialidades/*").hasAuthority("ROLE_ESPECIALISTA")
                // Horarios (HU-09, HU-10, HU-11, HU-22)
                .requestMatchers(HttpMethod.GET, "/api/horarios/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/horarios").hasAuthority("ROLE_ESPECIALISTA")
                .requestMatchers(HttpMethod.PUT, "/api/horarios/*", "/api/horarios/*/bloquear", "/api/horarios/*/desbloquear")
                        .hasAuthority("ROLE_ESPECIALISTA")
                // Citas (HU-12 a HU-15): solo pacientes
                .requestMatchers(HttpMethod.POST, "/api/citas").hasAuthority("ROLE_USUARIO")
                .requestMatchers(HttpMethod.GET, "/api/citas/usuario/*").hasAuthority("ROLE_USUARIO")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/cancelar", "/api/citas/*/reprogramar").hasAuthority("ROLE_USUARIO")
                // Informes y acceso (HU-19, HU-24)
                .requestMatchers(HttpMethod.GET, "/api/informes/general").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/acceso/mi-rol").authenticated()
                // Todo lo demás está cerrado
                .anyRequest().denyAll()
            )
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



