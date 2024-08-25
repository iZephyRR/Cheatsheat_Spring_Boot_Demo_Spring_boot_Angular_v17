package com.cheatsheet.controller;

import com.cheatsheet.component.UnsupportedMediaTypeException;
import com.cheatsheet.component.VideoProcessor;
import com.cheatsheet.dto.*;
import com.cheatsheet.entity.CategoryEntity;
import com.cheatsheet.entity.CheatsheetEntity;
import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.services.*;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("cheatsheet")
public class CheatsheetController {

    @Autowired
    private MapperService mapper;

    @Autowired
    private Message message;

    @Autowired
    private CheatsheetService sheetService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NotificationController notificationController;

    //private final MyTelegramBot telegramBot;
    private static final Logger logger = LoggerFactory.getLogger(CheatsheetController.class);

    private final TelegramUserService telegramUserService;

    @Autowired
    private CheatsheetController(@Lazy TelegramUserService telegramUserService) {
        //this.telegramBot = telegramBot;
        this.telegramUserService = telegramUserService;
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CheatsheetDTO>> getAllCheatsheets() {
        List<CheatsheetDTO> sheetDtoList = sheetService.getAllCheatsheets();
        return ResponseEntity.ok(sheetDtoList);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> createCheatsheet(@ModelAttribute CheatsheetDTO sheetDto, @RequestHeader("Authorization") String authHeader) throws IOException {
        int loggedInUser = jwtService.extractUserId(authHeader);
        String pdfUrl = saveFileAndGetUrl(sheetDto.getPdfFile());
        sheetDto.setFileUrl(pdfUrl);
        UserEntity user = userService.getById(loggedInUser);
        CategoryEntity category = categoryService.getById(sheetDto.getCategoryId());
        CheatsheetEntity entity = mapper.convertToSheetEntity(sheetDto);
        entity.setUserEntity(user);
        entity.setCategory(category);

        CheatsheetEntity successEntity = sheetService.insertCheatsheet(entity);
        if (successEntity != null && successEntity.getTitle() != null) {
            String creatorName = user.getName();
            String cheatsheetTitle = successEntity.getTitle();
            notificationController.sendNotification(creatorName, cheatsheetTitle);
            String chatId = telegramUserService.getChatIdByUsername("oetsu_34");
//            telegramUserService.sendDocument(chatId,successEntity.getFileUrl(), successEntity.getSummary(),successEntity.getUserEntity().getName());
            message.setMessage("Creating Cheatsheet Successful");
        } else {
            message.setMessage("Creating Cheatsheet Fail");
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping(value = "/getCheatsheetByCreatedBy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CheatsheetDTO>> getCheatsheetByUserId(@RequestHeader("Authorization") String authHeader) {
        int loggedInUser = jwtService.extractUserId(authHeader);
        List<CheatsheetDTO> sheetDtoList = sheetService.getCheatsheetsByCreatedBy(loggedInUser);
        return ResponseEntity.ok(sheetDtoList);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> deleteCheatsheet(@PathVariable("id") int sheetId) {
        int success = sheetService.softDelete(sheetId);
        if (success == 1) {
            message.setMessage("Delete Successful");
        } else {
            message.setMessage("Delete Fail");
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping(value = "/update/cheatsheet/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CheatsheetDTO> getByCheatsheetId(@PathVariable("id") int sheetId) {
        CheatsheetEntity entity = sheetService.getById(sheetId);
        return ResponseEntity.ok(mapper.convertToSheetDTO(entity));
    }

    @PutMapping(value = "/update/cheatsheet/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> updateCheatsheet(@ModelAttribute CheatsheetDTO sheetDto, @PathVariable("id") int sheetId) throws IOException {
        String existPdfUrl = sheetService.getPdfUrl(sheetId);
        String existFilename = sheetService.getFilename(sheetId);
        String newPdfUrl;
        if (sheetDto.getPdfFile() != null) {
            newPdfUrl = saveFileAndGetUrl(sheetDto.getPdfFile());
            deleteFile(existPdfUrl);
        } else {
            sheetDto.setFilename(existFilename);
            newPdfUrl = existPdfUrl;
        }
        sheetDto.setFileUrl(newPdfUrl);
        CategoryEntity category = categoryService.getById(sheetDto.getCategoryId());
        CheatsheetEntity entity = mapper.convertToSheetEntity(sheetDto);
        entity.setCategory(category);
        CheatsheetEntity updateEntity = sheetService.updateSheet(entity);
        if (updateEntity != null && updateEntity.getTitle() != null) {
            message.setMessage("Update Successful.");
        } else {
            message.setMessage("Update Fail");
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping(value = "/getCheatsheetsByCategory/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CheatsheetDTO>> getCheatsheetsByCategory(@PathVariable("id") int cateId) {
        List<CheatsheetDTO> sheetDtoList = sheetService.getCheatsheetsByCategory(cateId);
        return ResponseEntity.ok(sheetDtoList);
    }

    @PostMapping(value = "/send-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> sendDocument(@ModelAttribute SendToCustomTelegram toCustomTelegram, @RequestHeader("Authorization") String authHeader) throws IOException, TelegramApiException {
        int loggedInUser = jwtService.extractUserId(authHeader);
        logger.info("loggedIn User : {}", loggedInUser);
        UserEntity user = userService.getById(loggedInUser);
        MultipartFile file = toCustomTelegram.getPdfFile();
        String url = saveFileAndGetUrl(file);
        logger.info("file url : {}", url);
        String username = toCustomTelegram.getUsername();
        if (username.startsWith("@")) {
            username = username.substring(1);
        }
        toCustomTelegram.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        Long chatId = Long.valueOf(telegramUserService.getChatIdByUsername(username));
        logger.info("chat id : {}", chatId);
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("audio/")) {
                telegramUserService.sendAudio(chatId, url, toCustomTelegram.getFilename(), user.getName());
            } else if (contentType.startsWith("video/")) {
                telegramUserService.sendVideo(chatId, url, toCustomTelegram.getFilename(), user.getName());
            } else if (contentType.equals("application/pdf")) {
                telegramUserService.sendDocument(chatId, url, toCustomTelegram.getFilename(), user.getName());
            } else if (contentType.equals("application/vnd.ms-excel") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                telegramUserService.sendDocument(chatId, url, toCustomTelegram.getFilename(), user.getName());
            } else if (contentType.startsWith("image/")) {
                // Reduce image quality before sending
                byte[] reducedImage = reduceImageResolution(file, 800, 600);
                url = saveFileAndGetUrl(reducedImage, "reduced_image.jpg");
                telegramUserService.sendImage(chatId, url, toCustomTelegram.getFilename(), user.getName());
            } else {
                throw new UnsupportedMediaTypeException("Unsupported file type: " + contentType);
            }
        }


        message.setMessage("Send Successful to @" + toCustomTelegram.getUsername());
        return ResponseEntity.ok(message);
    }


    @PostMapping(value = "/send-poll")
    public ResponseEntity<Message> sendPoll(@RequestBody ForPoll forPoll) throws TelegramApiException {
        //System.out.println("username : " + forPoll.getUsername());
        Long chatId = Long.valueOf(telegramUserService.getChatIdByUsername(forPoll.getUsername()));
        //System.out.println("chat id : " +chatId);
        telegramUserService.sendPoll(chatId, forPoll.getQuestion(), forPoll.getOptions());
        message.setMessage("send poll success");
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/send-message")
    public ResponseEntity<Message> sendMessage(@RequestBody TelegramDTO telegramDTO) throws TelegramApiException {
        Long chatId = Long.valueOf(telegramUserService.getChatIdByUsername(telegramDTO.getUsername()));
        telegramUserService.sendMessage(chatId, telegramDTO.getMessage());
        message.setMessage("message send success");
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "change-bot-profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Message> changeProfile (@ModelAttribute MultipartFile photo) throws IOException, TelegramApiException {
        String url = saveFileAndGetUrl(photo);
        message.setMessage(telegramUserService.setProfile(url));
        return ResponseEntity.ok(message);
    }

    private String saveFileAndGetUrl(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            logger.info("Original filename: " + originalFilename);
            String filename = UUID.randomUUID().toString() + "_" + originalFilename;
            String uploadDir = "./uploads"; // Directory where you want to store uploaded files

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            return filePath.toAbsolutePath().toString();
        }
        return null;
    }

    // Method for saving reduced image to disk
    private String saveFileAndGetUrl(byte[] fileBytes, String filename) throws IOException {
        String uploadDir = "./uploads";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, fileBytes);
        return filePath.toAbsolutePath().toString();
    }

    // Method for reducing image resolution
    public byte[] reduceImageResolution(MultipartFile imageFile, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(imageFile.getInputStream())
                .size(width, height)
                .outputFormat("jpg")
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    private void deleteFile(String filePath) throws IOException {
        if (filePath != null) {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

}
