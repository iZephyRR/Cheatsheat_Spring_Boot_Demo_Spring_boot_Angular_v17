package com.cheatsheet.controller;

import com.cheatsheet.dto.CheatsheetNotification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendNotification(String creator, String cheatsheetTitle) {
        CheatsheetNotification notification = new CheatsheetNotification(creator, cheatsheetTitle);
        System.out.println("NotificationController : " + notification.getCheatsheetTitle());
        simpMessagingTemplate.convertAndSend("/topic/notifications", notification);
    }



}
