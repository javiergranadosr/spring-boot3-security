package com.example.security.filter;

import com.example.security.jwt.JwtUtil;
import com.example.services.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    // Metodo encargado de validar, si la petecion que se esta realizando tiene un token valido. Si es asi nos autenticamos correctamente y podemos acceder al recurso API
    @Override
    protected void doFilterInternal(
            @NonNull  HttpServletRequest request,
            @NonNull  HttpServletResponse response,
            @NonNull  FilterChain filterChain
    ) throws ServletException, IOException {
        // Obtenemos token de la peticion y validamos
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                // Obtiene datos del usuario, de nuestra clase service  UserDetailServiceImpl que implementa la interfaz UserDetailsService
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                // Nos autenticamos
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null,  userDetails.getAuthorities());
                // Obtenemos contexto del usuario que se ha autenticado (hizo login)
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // No permite el acceso en caso de no ser un token valido.
        filterChain.doFilter(request, response);
    }
}
