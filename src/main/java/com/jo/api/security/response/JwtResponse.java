package com.jo.api.security.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String alias;
    private String userKey;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String alias, String userKey, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.alias = alias;
        this.userKey = userKey;
        this.roles = roles;
    }
}

