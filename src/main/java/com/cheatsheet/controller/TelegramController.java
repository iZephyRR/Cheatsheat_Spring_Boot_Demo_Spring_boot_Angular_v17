package com.cheatsheet.controller;

import com.cheatsheet.config.MyTelegramBot;
import com.cheatsheet.dto.TelegramDTO;
import com.cheatsheet.services.TelegramService;
import com.cheatsheet.services.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telegram")
public class TelegramController {



    @Autowired
    private TelegramUserService teleUserService;

    @Autowired
    private TelegramService telegramService;

    private static final Logger logger = LoggerFactory.getLogger(TelegramController.class);

//    @GetMapping("/send-message")
//    public String sendMessage(@RequestBody TelegramDTO telegramDTO) {
//        String username = telegramDTO.getUsername();
//        if (username.startsWith("@")) {
//            username = username.substring(1);
//        }
//        logger.info("Processed username: {}", username);
//        //teleUserService.setTestData();
//        String chatId = teleUserService.getChatIdByUsername(username);
//        if (chatId != null) {
//            logger.info("Sending message to chat ID: {}", chatId);
//            telegramBot.sendMessage(chatId, telegramDTO.getMessage());
//            return "Message sent to " + telegramDTO.getUsername();
//        } else {
//            logger.error("User not found or not interacted with the bot.");
//            return "User not found or not interacted with the bot.";
//        }
//    }

}
