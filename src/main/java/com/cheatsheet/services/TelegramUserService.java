package com.cheatsheet.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.CreateChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TelegramUserService extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    private final String botUsername;
    private final String botToken;

    //private final AbsSender absSender;

    @Autowired
    public TelegramUserService(String botUsername,
                               String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;

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
//        System.out.println(update);
        if (update.hasPollAnswer()) {
            System.out.println("Poll answer received");
            handlePollAnswer(update.getPollAnswer());
        } else if (update.hasMessage()) {
            registerUser(update);
        } else {
            System.out.println("No PollAnswer found in the update.");
        }
    }

    public void sendMessagesInBatches(List<Long> chatIds, String message) {
        int batchSize = 30;
        int delay = 1; // 1-second delay between batches
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < chatIds.size(); i += batchSize) {
            List<Long> batch = chatIds.subList(i, Math.min(i + batchSize, chatIds.size()));
            executor.schedule(() -> {
                for (Long chatId : batch) {
                    try {
                        sendMessage(chatId,message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, delay * (i / batchSize), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    @Async
    public void registerUser(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            System.out.println(message);
            long chatId = message.getChatId();
            if (message.getChat().isGroupChat() || message.getChat().isSuperGroupChat()) {
//                String groupTitle = message.getChat().getTitle();
//                System.out.println("Group Title: " + groupTitle);
//                System.out.println("Group Chat ID: " + chatId);
//                //sendMessage(chatId, "Hello Everyone");
//                handleGroupMessage(chatId, groupTitle, message.getText());
            } else if (message.getChat().isUserChat()) {
                // Handle individual user chat
                String username = message.getChat().getUserName();
                System.out.println("User Username: " + username);
                System.out.println("User Chat ID: " + chatId);
                System.out.println("text : " + message.getText());
                //sendMessage(chatId, "Hello Nice To Meet You!!");
//                // Process or respond to the user message
//                handleUserMessage(chatId, username, message.getText());
            }
        }
    }

    private void handleGroupMessage(long chatId, String groupTitle, String messageText) {
        //System.out.println("Received a message in group '" + groupTitle + "': " + messageText);
    }

    private void handleUserMessage(long chatId, String username, String messageText) {
        //System.out.println("Received a message from user '" + username + "': " + messageText);
    }

    private void handlePollAnswer(PollAnswer pollAnswer) {
        Long userId = pollAnswer.getUser().getId();
        String pollId = pollAnswer.getPollId();
        List<Integer> optionIds = pollAnswer.getOptionIds();
        String answer = pollAnswer.getOptionIds().toString();

        System.out.println("User ID: " + userId);
        System.out.println("Poll ID: " + pollId);
        System.out.println("Chosen Option IDs: " + optionIds.toString());
        System.out.println("Answer : " + answer);

    }

    public String getChatIdByUsername(String username) {
        return userService.getChatIdByUsername(username);
    }

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }

    public void sendPoll(Long chatId, String question, String... options) throws TelegramApiException {
        SendPoll poll = SendPoll.builder()
                .chatId(chatId)
                .question(question)
                .options(Arrays.asList(options))
                .isAnonymous(false).build();
        execute(poll);
    }

    public void sendDocument(Long chatId, String filePath, String description, String creator) throws TelegramApiException {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        InputFile pdfFile = new InputFile(new File(filePath));
        sendDocumentRequest.setDocument(pdfFile);
        sendDocumentRequest.setCaption(description + "\n" + creator);
        execute(sendDocumentRequest);
    }

    public void sendAudio(Long chatId, String fileUrl, String filename, String senderName) throws TelegramApiException {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId.toString());
        sendAudio.setAudio(new InputFile(new File(fileUrl)));
        sendAudio.setCaption("Sent by: " + senderName + "\nFilename: " + filename);
        execute(sendAudio);
    }

    public void sendVideo(Long chatId, String fileUrl, String filename, String senderName) throws TelegramApiException {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId.toString());
        sendVideo.setVideo(new InputFile(new File(fileUrl)));
        sendVideo.setCaption("Sent by: " + senderName + "\nFilename: " + filename);
        execute(sendVideo);
    }

    public void sendImage(Long chatId, String fileUrl, String filename, String senderName) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(new File(fileUrl)));
        sendPhoto.setCaption("Sent by: " + senderName + "\nFilename: " + filename);
        execute(sendPhoto);
    }

    public String setProfile(String photoFilePath) throws TelegramApiException {
        SetChatPhoto setChatPhoto = new SetChatPhoto();
        setChatPhoto.setChatId(getMe().getId().toString());
        System.out.println("setChatPhoto.getChatId() : " + setChatPhoto.getChatId());
        setChatPhoto.setPhoto(new InputFile((new File(photoFilePath))));
        execute(setChatPhoto);
       return "Profile picture updated successfully." ;
    }

    public void addUsersToGroup(Long groupChatId , Long userChatId) throws TelegramApiException{

    }


}
