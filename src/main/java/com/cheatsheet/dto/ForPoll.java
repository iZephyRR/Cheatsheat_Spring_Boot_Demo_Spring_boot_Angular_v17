package com.cheatsheet.dto;

import lombok.Data;

@Data
public class ForPoll {
    private String username;
    private String question;
    private String[] options;
}
