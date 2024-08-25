package com.cheatsheet.services;


import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    public void sendMessage(String to, String from, String body) {
        Message message = Message.creator(
                new PhoneNumber(to),  // To number
                new PhoneNumber(from),  // From number
                body  // Message body
        ).create();
    }

}
