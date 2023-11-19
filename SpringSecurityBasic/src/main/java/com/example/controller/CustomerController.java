package com.example.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1")
public class CustomerController {

    @Autowired
    SessionRegistry sessionRegistry;

    @GetMapping("/index")
    public String index() {
        return "Hello world";
    }

    @GetMapping("/index2")
    public String index2() {
        return "Hello world not SECURED!";
    }

    @GetMapping("/session")
    public ResponseEntity<?> getDetailsSession(){
        String sessionId = "";
        User user = null;
        List<Object> sessions = sessionRegistry.getAllPrincipals(); // Obtenemos todas las sesiones registradas
        for (Object s:sessions) {
            if (s instanceof  User) {
                user = (User) s;
            }
            // Obtiene la informacion de todas las sesiones obtenidas de getAllPrincipals()
            List<SessionInformation> sessionInformations =  sessionRegistry.getAllSessions(user, false);
            for(SessionInformation sessionInformation: sessionInformations) {
                sessionId = sessionInformation.getSessionId();
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("response", "Hello world");
        response.put("sessionId", sessionId);
        response.put("sessionUser", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
