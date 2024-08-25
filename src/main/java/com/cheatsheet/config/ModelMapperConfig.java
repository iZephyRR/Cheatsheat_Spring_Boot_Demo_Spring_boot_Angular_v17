package com.cheatsheet.config;

import com.cheatsheet.dto.Message;
import com.cheatsheet.dto.AuthenticationResponse;
import com.cheatsheet.filter.JWTAuthenticationFilter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Message message(){
        return new Message();
    }

    @Bean
    public AuthenticationResponse authResponse () {
        return  new AuthenticationResponse();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticatoinFilter () {
        return new JWTAuthenticationFilter();
    }




}
