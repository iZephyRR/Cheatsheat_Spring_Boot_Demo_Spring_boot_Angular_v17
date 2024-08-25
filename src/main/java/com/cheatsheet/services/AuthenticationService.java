package com.cheatsheet.services;

import com.cheatsheet.dto.AuthenticationResponse;
import com.cheatsheet.dto.LoginDTO;
import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MapperService mapperService;

    @Autowired
    AuthenticationResponse authenticationResponse;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public AuthenticationResponse authenticate (LoginDTO loginDTO) {
        try {
            UserEntity userEntity = userRepository.authenticate(loginDTO.getEmail());
            if (passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword())) {
                if (userEntity.getIsBanned() != 1) {
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginDTO.getEmail(), loginDTO.getPassword()
                            )
                    );
                    String token = jwtService.generateToken(userEntity);
                    authenticationResponse.setMessage("Login Successful");
                    authenticationResponse.setToken(token);
                } else {
                    authenticationResponse.setMessage("You has been banned from accessing the system");
                }
            } else {
                authenticationResponse.setMessage("Incorrect Password!!");
            }
        }catch (Exception e) {
            authenticationResponse.setMessage("User not found " + e.getMessage());
            throw e;
        }
        return authenticationResponse;
    }



}
