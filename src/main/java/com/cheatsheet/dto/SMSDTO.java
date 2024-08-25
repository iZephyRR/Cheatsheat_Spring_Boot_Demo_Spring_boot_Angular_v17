package com.cheatsheet.dto;

import lombok.Data;

@Data
public class SMSDTO {
    private String to;
    private String from;
    private String message;
}
