package com.cheatsheet.config;

import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.repository.UserRepository;
import com.cheatsheet.services.TelegramUserService;
import com.cheatsheet.services.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MyTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final TelegramUserService userService;
    //private final Map<String, String> usernameToChatIdMap = new HashMap<>();

    public MyTelegramBot(String botUsername, String botToken,@Lazy TelegramUserService userService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        userService.registerUser(update);
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocument(String chatId, String filePath, String description, String creator) {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        InputFile pdfFile = new InputFile(new File(filePath));
        sendDocumentRequest.setDocument(pdfFile);
        sendDocumentRequest.setCaption(description + "\n" + creator);
        try {
            execute(sendDocumentRequest);
            //System.out.println(execute(sendDocumentRequest));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}
