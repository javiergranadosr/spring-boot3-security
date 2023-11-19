package com.example.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/api")
public class TestRoleController {


    @GetMapping("/access-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String accessAdmin(){
        return "Hello admin";
    }

    @GetMapping("/access-user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String accessUser(){
        return "Hello user";
    }

    @GetMapping("/access-guest")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")
    public String accessGuest(){
        return "Hello guest";
    }
}
