package com.cheatsheet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name= "user")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" ,nullable = false)
    private int id;

    @Column(name = "name" ,nullable = false , length = 20)
    private String name;

    @Column(name = "email" ,nullable = false , unique = true , length = 30)
    private String email;

    @Column(name = "password" ,nullable = false ,length = 100)
    private String password;

    @Column(name = "telegram_username" , nullable = false , length = 20)
    private String telegramUsername;

    @Column( name = "telegram_userId", nullable = true, length = 20)
    private String telegramUserId;

    @Lob
    @Column(name = "image" , columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(name="is_Banned",nullable = false,columnDefinition = "TINYINT(1)")
    private int isBanned = 0 ;
}
