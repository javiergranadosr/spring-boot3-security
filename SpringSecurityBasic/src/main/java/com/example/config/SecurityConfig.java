package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * CLASE PARA CONFIGURAR LA SEGURIDAD DE NUESTRA APLICACION EN SPRING SECURITY
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * CONFIGURACION PARA VERSIONES ANTERIORES
     * INTERFAZ DE SPRING SECURITY, QUE NOS PERMITE CONFIGURAR LA SEGURIDAD
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //IMPLEMENTACION OTRAS VERSIONES DE SPRING
                .csrf().disable() // CROSS-SITE REQUEST FORGERY SOLO UTILIZAR AL GENERAR APIS
                .authorizeHttpRequests()
                   .requestMatchers("/v1/index2").permitAll()
                    .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                //.httpBasic()// PERMITE ENVIAR LAS CREDENCIALES (usuario, password) EN EL HEADER DE LA APLICACION, caso contrario tendremos que iniciar sesion en el formulario
                // de la pagina de la aplicacion y no permite el envio de credencias por la cabecera header. (USO IDEAL PARA CUANDO SE TRABAJA EN APIS)
                //.and()
                .build();
    }*/

    // Configuracion lambda, para nuevas versiones
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/v1/index2").permitAll(); // Configuracion de URLS
                    authorize.anyRequest().authenticated(); // Cualquier otra peticion tiene que estar autenticado
                })
                .formLogin(formLogin -> {
                    formLogin.successHandler(successHandler()); // URL a donde se va a ir despues de iniciar sesion
                    formLogin.permitAll();
                })
                .sessionManagement( sm -> {
                    // ALWAYS: Crea sesion, siempre y cuando no exista ninguna, si hay una existe se reutiliza
                    // IF_REQUIRED: Crea una sesion, solo si es necesario, ejemplo: si la sesion no existe la crea, si existe la reutiliza (evalua si es ncesario crear la sesion)
                    // NEVER: No crea ninguna sesion, pero si ya existe la reutiliza, si no existe no usa sesion y ni crea
                    // STATELESS: No trabaja con sesiones
                    sm.sessionCreationPolicy(SessionCreationPolicy.ALWAYS); // OPCIONES: ALWAYS - IF_REQUIRED - NEVER - STATELESS
                    sm.invalidSessionUrl("/login"); // Si el usuario no inicia sesion correctamente y no se crea una sesion, lo enviamos a la URL configurada
                    sm.maximumSessions(1); // Solo permitimos una sesion por usuario en la aplicacion
                    sm.sessionFixation( sf -> {
                        // OPCIONES:  sf.migrateSession(); - sf.newSession(); - none()
                        sf.migrateSession(); // Esto se usa, para que si spring detecta que se ha fijado una sesion, se genera una nueva (mantiene los datos de la ultima sesion)
                        //sf.newSession(); // Esto se usa, para que si spring detecta que se ha fijado una sesion, se genera una nueva (no mantiene los datos de la ultima sesion)
                        //sf.none(); // Inabilita la sessionFixation()
                    }); // No permite que se fijen los id de sesiones en caso de un ataque
                }) // Manejar el comportamiento de las sesiones
                .build();
    }

    /**
     * Si se inicia sesion correctamente, el sistema nos va redirigir a la url configurada
     * @return
     */
    public AuthenticationSuccessHandler successHandler() {
        return (( req, rep, auth ) -> {
            rep.sendRedirect("/v1/session");
        });
    }

    /**
     * Permite obtener informacion de la sesion de los usuarios en la APP
     * @return
     */
    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }
}
