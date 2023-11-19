package com.example.security;

import com.example.security.filter.JwtAuthenticationFilter;
import com.example.security.filter.JwtAuthorizationFilter;
import com.example.security.jwt.JwtUtil;
import com.example.services.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Utilizado para usar la anotacion  @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')") en los controladores
public class SecurityConfig {

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    // Metodo para configurar la seguridad de la API (Comportamiento) (CONFIGURACION UTILIZADA PARA APIS)
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login"); // Opcional, ya que es la URL por default, pero se la podemos cambiar para nuestro login de nuestra API

        return httpSecurity
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/v1/api/hello").permitAll();
                    auth.anyRequest().authenticated(); // Las demas rutas se tiene  que iniciar sesion
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS); // No vamos a manejar sesiones, debido a que se trabajara una API
                })
                .addFilter(jwtAuthenticationFilter) // Filtro para el inicio de sesion en la API
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Filtro el cual valida token, cuando un usuario ya ha ingresado sus credenciales
                .build();
    }

    // Metodo para crear un usuario en memoria (OPCIONAL)
    /*@Bean
    UserDetailsService userDetailsService() {
        // Utilizado para crear un usuario en memoria (Opcional)
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                User.withUsername("Javier")
                        .password("1234")
                        .roles()
                        .build()
        );
        return manager;
    }*/

    // Metodo encargado de encriptar passwords
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Metodo que se encarga de la administracion de la autenticacion de usuarios en nuestra API
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and().build();
    }
}
