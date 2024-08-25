package com.cheatsheet.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SendToCustomTelegram {
    private String username;
    private MultipartFile pdfFile;
    private String filename;
}
