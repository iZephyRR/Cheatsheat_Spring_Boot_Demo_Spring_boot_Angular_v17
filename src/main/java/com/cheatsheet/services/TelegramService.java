package com.cheatsheet.services;

import com.cheatsheet.dto.TelegramResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TelegramService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate;

    public TelegramService() {
        this.restTemplate = new RestTemplate();
    }

    public Long getUserIdByUsername (String username) {

        if (username.startsWith("@")) {
            username = username.substring(1);
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://api.telegram.org/bot{botToken}/getChat")
                .queryParam("chat_id", "@" + username)
                .buildAndExpand(botToken)
                .toUriString();
        logger.info("Fetching user ID for username: {}", username);

        try {
            TelegramResponse response = restTemplate.getForObject(url, TelegramResponse.class);
            if (response != null && response.isOk()) {
                logger.info("Successfully fetched user ID for username: {}", username);
                return response.getResult().getId();
            } else if (response != null) {
                logger.error("Failed to get chat info for username {}: {}", username, response.getDescription());
                throw new RuntimeException("Failed to get chat info: " + response.getDescription());
            } else {
                logger.error("Error fetching user ID: Unknown error occurred");
                throw new RuntimeException("Error fetching user ID: Unknown error occurred");
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching user ID: {} {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Error fetching user ID: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching user ID: {} {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Server error fetching user ID: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Unknown error fetching user ID: {}", e.getMessage(), e);
            throw new RuntimeException("Unknown error fetching user ID: " + e.getMessage(), e);
        }
    }

    public void sendMessageToUser(String userId, String message) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.telegram.org/bot{botToken}/sendMessage")
                .queryParam("chat_id", userId)
                .queryParam("text", message)
                .buildAndExpand(botToken)
                .toUriString();
        logger.info("Sending message to user ID: {}", userId);

        try {
            restTemplate.getForObject(url, String.class);
            logger.info("Message sent successfully to user ID: {}", userId);
        } catch (HttpClientErrorException e) {
            logger.error("Error sending message: {} {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Error sending message: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            logger.error("Server error sending message: {} {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Server error sending message: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Unknown error sending message: {}", e.getMessage(), e);
            throw new RuntimeException("Unknown error sending message: " + e.getMessage(), e);
        }
    }

    public void checkUserInteraction() {
        String url = String.format("https://api.telegram.org/bot%s/getUpdates", botToken);
        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.info("Bot Updates: {}", response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error checking updates: {} {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Unknown error checking updates: {}", e.getMessage(), e);
        }
    }




}
