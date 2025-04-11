package com.jo.api.security.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {

    private String accessToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}

