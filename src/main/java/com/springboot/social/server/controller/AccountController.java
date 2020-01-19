package com.springboot.social.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
public class AccountController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @PostMapping(value = "/oauth/token")
    public ResponseEntity<OAuth2AccessToken> postAccessToken(
            @RequestHeader(name = "x-auth-token", required = false) String xAuthToken,
            @RequestParam Map<String, String> parameters,
            Principal principal
    ) throws HttpRequestMethodNotSupportedException {
        log.info("AccountController");
        parameters.putIfAbsent("grant_type", "password");
        if (xAuthToken != null) {
            parameters.put("provider_token", xAuthToken);
        }
        return tokenEndpoint.postAccessToken(principal, parameters);
    }

    @GetMapping(value = "/api/me")
    public ResponseEntity<Object> me(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}
