package com.cheatsheet.controller;

import com.cheatsheet.dto.SMSDTO;
import com.cheatsheet.services.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class SMSController {

    @Autowired
    private SMSService smsService;

    @PostMapping (value = "/sendSMS", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sendMessage(@RequestBody SMSDTO smsdto) {
        System.out.println("here in viber controller");
        smsService.sendMessage(smsdto.getTo(), smsdto.getFrom(), smsdto.getMessage());
        return "Message sent successfully";
    }

}
