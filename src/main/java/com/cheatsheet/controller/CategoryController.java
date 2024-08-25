package com.cheatsheet.controller;

import com.cheatsheet.dto.Message;
import com.cheatsheet.dto.CategoryDTO;
import com.cheatsheet.entity.CategoryEntity;
import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.services.CategoryService;
import com.cheatsheet.services.JWTService;
import com.cheatsheet.services.MapperService;
import com.cheatsheet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService cateService;

    @Autowired
    private UserService userService;

    @Autowired
    private MapperService mapper;

    @Autowired
    private Message message;

    @Autowired
    private JWTService jwtService;

    @PostMapping(value = "create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> createCategory(@RequestBody CategoryDTO dto,
                                                  @RequestHeader("Authorization") String authHeader) {
        int id = jwtService.extractUserId(authHeader);
        System.out.println("loggedIn user id : " + id);
        UserEntity userEntity = userService.getById(id);
        if (userEntity != null && userEntity.getName() != null) {
            CategoryEntity mappedEntity = mapper.convertToCategoryEntity(dto);
            mappedEntity.setUser(userEntity);
            CategoryEntity alreadyExist = cateService.findByName(dto.getName());
            cateService.insertCategory(mappedEntity);
            message.setMessage("New Category Created");
        } else {
            message.setMessage("User with ID" + id + " not Found");
        }
        return ResponseEntity.ok(message);
    }

//    @PostMapping(value = "/create" , produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> createCategory(
//            @RequestBody CategoryDTO categoryDTO,
//            @RequestHeader("Authorization") String authHeader,
//            @RequestHeader("LoginUser") String userId
//            ) {
//        System.out.println(categoryDTO);
//        int id = jwtService.extractUserId(authHeader);
//        System.out.println("extractUserId : " + id);
//        System.out.println("userid send from angular: " +userId);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Category created successfully");
//    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategoryDTO> getAllCategory() {
        List<CategoryEntity> entityList = cateService.getAll();
        return mapper.convertToCategoryDTOList(entityList);
    }

}
