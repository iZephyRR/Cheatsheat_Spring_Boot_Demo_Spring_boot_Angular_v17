package com.cheatsheet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" ,nullable = false)
    private int id;

    @Column(name = "name" ,nullable = false , length = 50, unique = true)
    private String name;

    @JoinColumn(name = "createdBy")
    @ManyToOne(cascade = CascadeType.MERGE)
    private UserEntity user;
}
