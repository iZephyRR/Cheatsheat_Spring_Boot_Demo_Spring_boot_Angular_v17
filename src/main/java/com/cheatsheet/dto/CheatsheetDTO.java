package com.cheatsheet.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;

@Data
public class CheatsheetDTO {
    private int id;
    private String title;
    private String summary;
    private int categoryId;
    private MultipartFile pdfFile;
    private MultipartFile imageFile;
    private String filename;
    private String fileUrl;
    private String image;
    private Boolean is_Deleted;
    private String createdBy;
    private String category;
    private Date createdAt;
    private Date updatedAt;
}
