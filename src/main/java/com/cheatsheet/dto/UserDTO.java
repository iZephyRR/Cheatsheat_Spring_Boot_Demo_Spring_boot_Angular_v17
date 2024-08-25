package com.cheatsheet.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String password;
    private String image;
    private String role;
    private int is_Banned;
    private String otp;
}
