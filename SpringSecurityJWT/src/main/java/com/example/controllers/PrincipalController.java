package com.example.controllers;

import com.example.Erole;
import com.example.dto.UserDto;
import com.example.models.RoleEntity;
import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v1/api")
public class PrincipalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    public String hello() {
        return "Hello not secured";
    }

    @GetMapping("/hello-secured")
    public String helloSecured() {
        return "Hello secured";
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto data) {
        Set<RoleEntity> roles = data.getRoles().stream()
                .map(role -> RoleEntity.builder()
                                .name(Erole.valueOf(role))
                                .build()
                ).collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(data.getUsername())
                .password(passwordEncoder.encode(data.getPassword()))
                .email(data.getEmail())
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok(userEntity);
    }

    @DeleteMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "Usuario borrado correctamente con el id: ".concat(String.valueOf(id));
    }
}
