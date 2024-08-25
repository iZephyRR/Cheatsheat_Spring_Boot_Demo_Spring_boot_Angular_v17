package com.cheatsheet.services;

import com.cheatsheet.entity.CategoryEntity;
import com.cheatsheet.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository cateRepo;

    @Transactional(rollbackFor = Exception.class)
    public void insertCategory(CategoryEntity entity){
        try {
            cateRepo.save(entity);
        }catch (Exception e) {
            throw e;
        }
    }

    public CategoryEntity findByName(String name){
        Optional<CategoryEntity> optionalCategoryEntity = cateRepo.findByName(name);
        CategoryEntity entity = new CategoryEntity();
        if (optionalCategoryEntity.isPresent()){
            entity = optionalCategoryEntity.get();
        }
        return entity;
    }

    public List<CategoryEntity> getAll(){
        return cateRepo.findAll();
    }

    public CategoryEntity getById(int id){
        Optional<CategoryEntity> optionalCategoryEntity = cateRepo.findById(id);
        CategoryEntity entity = new CategoryEntity();
        if (optionalCategoryEntity.isPresent()){
            entity = optionalCategoryEntity.get();
        }
        return entity;
    }
}
