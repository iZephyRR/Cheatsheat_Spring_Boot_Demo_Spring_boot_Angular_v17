package com.cheatsheet.services;

import com.cheatsheet.dto.CategoryDTO;
import com.cheatsheet.dto.CheatsheetDTO;
import com.cheatsheet.dto.UserDTO;
import com.cheatsheet.entity.CategoryEntity;
import com.cheatsheet.entity.CheatsheetEntity;
import com.cheatsheet.entity.Role;
import com.cheatsheet.entity.UserEntity;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private  final ModelMapper modelMapper;
    @Autowired
    public MapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // convertToSheetDTO
    public CheatsheetDTO convertToSheetDTO(CheatsheetEntity entity) {
        CheatsheetDTO dto = modelMapper.map(entity, CheatsheetDTO.class);
        dto.setImage(Base64.getEncoder().encodeToString(entity.getImage()));
        return dto;
    }

    // convertToSheetEntity
    public CheatsheetEntity convertToSheetEntity(CheatsheetDTO dto) throws IOException {
        CheatsheetEntity entity = modelMapper.map(dto, CheatsheetEntity.class);
        entity.setImage(dto.getImageFile().getBytes());
        return entity;
    }

    // convertToUserDTO
    public UserDTO convertToUserDTO(UserEntity entity){
        UserDTO dto = modelMapper.map(entity, UserDTO.class);
        dto.setImage(Base64.getEncoder().encodeToString(entity.getImage()));
        return dto;
    }

    // convertToUserEntity
    public UserEntity convertToUserEntity(UserDTO dto) {
       UserEntity entity = modelMapper.map(dto, UserEntity.class);
       entity.setRole(Role.USER);
       entity.setPassword(passwordEncoder.encode(dto.getPassword()));
       entity.setImage(Base64.getDecoder().decode(dto.getImage().split(",")[1].getBytes()));
       return entity;
    }

    // convertToUserDTOList
    public List<UserDTO> convertToUserDTOList(List<UserEntity> entityList){
        return entityList.stream().map(this::convertToUserDTO).collect(Collectors.toList());
    }

    // convertToCheatsheetDTOList
    public List<CheatsheetDTO> convertToCheatsheetDTOList(List<CheatsheetEntity> entityList){
        return entityList.stream().map(this::convertToSheetDTO).collect(Collectors.toList());
    }

    // convertToCategoryEntity
    public CategoryEntity convertToCategoryEntity(CategoryDTO dto){
        return modelMapper.map(dto, CategoryEntity.class);
    }

    // convertToCategoryDTO
    public CategoryDTO convertToCategoryDTO(CategoryEntity entity){
        return modelMapper.map(entity, CategoryDTO.class);
    }

    // convertToCategoryDTOList
    public List<CategoryDTO> convertToCategoryDTOList(List<CategoryEntity> entityList){
        return entityList.stream().map(this::convertToCategoryDTO).collect(Collectors.toList());
    }

    public List<CheatsheetDTO> mapToDTOList (List<Object[]> objList){
        return objList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CheatsheetDTO mapToDTO(Object[] row) {
        CheatsheetDTO dto = new CheatsheetDTO();
        dto.setId((Integer) row[0]);
        dto.setTitle((String) row[1]);
        dto.setSummary((String) row[2]);
        dto.setFilename((String) row[3]);
        dto.setFileUrl((String) row[4]);
        dto.setImage(Base64.getEncoder().encodeToString((byte[]) row[5]));
        dto.setCreatedBy((String) row[6]);
        dto.setCategory((String) row[7]);
        dto.setCategoryId((Integer) row[8]);
        dto.setCreatedAt((Date) row[9]);
        dto.setUpdatedAt((Date) row[10]);
        dto.setIs_Deleted((Boolean) row[11]);
        return dto;
    }

}
