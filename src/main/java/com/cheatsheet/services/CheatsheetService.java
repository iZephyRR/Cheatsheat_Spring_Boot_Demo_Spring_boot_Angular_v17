package com.cheatsheet.services;

import com.cheatsheet.dto.CheatsheetDTO;
import com.cheatsheet.entity.CheatsheetEntity;
import com.cheatsheet.repository.CheatsheetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CheatsheetService {
    @Autowired
    private CheatsheetRepository sheetRepo;

    @Autowired
    private MapperService mapper;

    public CheatsheetEntity insertCheatsheet(CheatsheetEntity entity){
        return sheetRepo.save(entity);
    }

    public CheatsheetEntity getById(int id) {
        Optional<CheatsheetEntity> optionalEntity = sheetRepo.findById(id);
        CheatsheetEntity entity;
        if(optionalEntity.isPresent()){
            entity = optionalEntity.get();
            return entity;
        } else  {
            throw new EntityNotFoundException("Cheatsheet Not Found!");
        }
    }

    public List<CheatsheetEntity> getAllSheet () {
        return sheetRepo.findAll();
    }

    @Transactional
    public CheatsheetEntity updateSheet(CheatsheetEntity entity){
        Optional<CheatsheetEntity> result = sheetRepo.findById(entity.getId());
        CheatsheetEntity exist = new CheatsheetEntity();
        if(result.isPresent()){
            exist = result.get();
            exist.setTitle(entity.getTitle());
            exist.setSummary(entity.getSummary());
            exist.setImage(entity.getImage());
            exist.setFilename(entity.getFilename());
            exist.setFileUrl(entity.getFileUrl());
            exist.setCategory(entity.getCategory());
        }
        return sheetRepo.save(exist);
    }

    @Transactional(readOnly = false)
    public List<CheatsheetDTO> getAllCheatsheets(){
        List<Object[]> results = sheetRepo.getCheatSheets();
        return mapper.mapToDTOList(results);
    }

    @Transactional(readOnly = true)
    public List<CheatsheetDTO> getCheatsheetsByCreatedBy(int createdBy){
        List<Object[]> results = sheetRepo.getCheatsheetsByCreatedBy(createdBy);
        return mapper.mapToDTOList(results);
    }

    @Transactional(readOnly = true)
    public List<CheatsheetDTO> getCheatsheetsByCategory(int cateId) {
        List<Object[]> results = sheetRepo.getCheatsheetsByCategory(cateId);
        return mapper.mapToDTOList(results);
    }

    @Transactional
    public int softDelete(int sheetId){
        return sheetRepo.deleteCheatsheet(1, sheetId);
    }

    @Transactional(readOnly = true)
    public String getPdfUrl(int id){
        return sheetRepo.getPdfUrl(id);
    }

    @Transactional(readOnly = true)
    public String getFilename(int id){
        return sheetRepo.getFilename(id);
    }






}
