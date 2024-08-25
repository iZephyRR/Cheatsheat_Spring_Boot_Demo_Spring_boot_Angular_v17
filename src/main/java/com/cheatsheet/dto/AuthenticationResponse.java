package com.cheatsheet.dto;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;
    private String message;

}
