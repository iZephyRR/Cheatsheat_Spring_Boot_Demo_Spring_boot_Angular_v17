package com.cheatsheet.controller;

import com.cheatsheet.dto.AuthenticationResponse;
import com.cheatsheet.dto.LoginDTO;
import com.cheatsheet.dto.UserDTO;
import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.services.AuthenticationService;
import com.cheatsheet.services.EmailService;
import com.cheatsheet.services.MapperService;
import com.cheatsheet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationResponse authenticationResponse;

    @PostMapping(value = "/login" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.authenticate(loginDTO));
    }

    // request otp
    @PostMapping(value = "/requestotp" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> requestOTP (@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = userService.generateOtp();
        userService.storeOtp(email, otp);
        emailService.sendOTPEmail(email, otp);
        return ResponseEntity.ok(otp);
    }

    // verifyOtp and save user
    @PostMapping(value = "/signup" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> register (@RequestBody UserDTO userDTO){
        if(userService.verifyOtp(userDTO.getEmail(), userDTO.getOtp())) {
            UserEntity userEntity = mapperService.convertToUserEntity(userDTO);
            userService.insertUser(userEntity);
            authenticationResponse.setMessage("Registered Successfully!!");
        }else {
            authenticationResponse.setMessage("Registered Failed");
        }
        return ResponseEntity.ok(authenticationResponse);
    }


}
