package com.cheatsheet.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
import java.time.LocalDate;

@Entity
@Table(name = "cheatsheet")
@Data
public class CheatsheetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" ,nullable = false)
    private int id;

    @Column(name = "title" ,nullable = false , length = 30)
    private String title;

    @Column(name = "summary" ,nullable = false , length = 100)
    private String summary;

    @Column(name = "pdfFile")
    private String filename;

    @Column(name = "fileUrl" ,nullable = false)
    private String fileUrl;

    @Lob
    @Column(name = "image" ,nullable = false , columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Column(name="is_deleted",nullable = false , columnDefinition = "TINYINT(1)")
    private int isDeleted = 0 ;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "createdBy" ,nullable = false)
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category" ,nullable = false)
    private CategoryEntity category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
