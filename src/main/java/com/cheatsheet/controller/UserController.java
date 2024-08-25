package com.cheatsheet.controller;

import com.cheatsheet.dto.UserDTO;
import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.services.JWTService;
import com.cheatsheet.services.MapperService;
import com.cheatsheet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MapperService mapper;

    @Autowired
    private JWTService jwtService;

    @GetMapping (value = "/showUsers" , produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> showUsers(){
        List<UserEntity> entityLst = userService.getAllUsers();
        return mapper.convertToUserDTOList(entityLst);
    }

    @GetMapping(value = "/profile" , produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getByUserId(@RequestHeader("Authorization") String authHeader) {
        int loggedInUser = jwtService.extractUserId(authHeader);
        UserEntity entity = userService.getById(loggedInUser);
        UserDTO dto =  mapper.convertToUserDTO(entity);
        return ResponseEntity.ok(dto);
    }


   

}
