package com.cheatsheet.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OTPDetails {
    private String otp;
    private LocalDateTime expiryTime;

    public OTPDetails(String otp, LocalDateTime expiryTime) {
        this.otp = otp;
        this.expiryTime = expiryTime;
    }
}
