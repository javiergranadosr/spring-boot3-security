package com.example.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    // Metodo encargado de crear un token de acceso
    public String generateAccesToken(String username){
        return Jwts.builder()
                .setSubject(username) // Usuario que va generar el token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creacion del token en milisegundos
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration))) // Fecha de expiracion del token (Momento actual en milisengundos mas el dia que le sumamos nosotros)
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256) // Firma del token encriptada con el algoritmo HS256
                .compact();
    }

    // Metodo encargado de validar el token de acceso
    public boolean isTokenValid(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch (Exception e){
            // Token invalido
            log.error("Token invalido, error: ".concat(e.getMessage()));
            return false;
        }
    }

    // Metodo encargado de obtener toda la informacion del token (Claims)
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Metodo de obtener solo un dato del token (Claim)
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction ) {
        Claims claims =  extractAllClaims(token);
        return claimsTFunction.apply(claims) ;
    }

    // Metodo encargado de obtener el username del token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // Obtener firma del token
    private Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
