package com.cheatsheet.repository;

import com.cheatsheet.entity.CheatsheetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CheatsheetRepository extends JpaRepository<CheatsheetEntity,Integer> {

    @Query(value = "CALL GetCheatsheets()", nativeQuery = true)
    List<Object[]> getCheatSheets();

    @Query(value = "CALL GetCheatsheetsByUserId(:id)", nativeQuery = true )
    List<Object[]> getCheatsheetsByCreatedBy(@Param("id") int createdBy);

    @Query(value = "CALL GetCheatsheetsByCategory(:id)" , nativeQuery = true )
    List<Object[]> getCheatsheetsByCategory(@Param("id") int cateId);

    @Modifying
    @Query("update CheatsheetEntity cs set cs.isDeleted =:num where cs.id=:id")
    int deleteCheatsheet(@Param("num") int num,@Param("id") int sheetId);

    @Query("select cs.fileUrl from CheatsheetEntity cs where cs.id=:id")
    String getPdfUrl(@Param("id") int id);

    @Query("select cs.filename from CheatsheetEntity cs where cs.id=:id")
    String getFilename(@Param("id") int id);

}
