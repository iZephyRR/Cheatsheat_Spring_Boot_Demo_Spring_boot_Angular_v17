package com.cheatsheet.services;

import com.cheatsheet.dto.OTPDetails;
import com.cheatsheet.entity.UserEntity;
import com.cheatsheet.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    private final Map<String, OTPDetails> otpStorage = new HashMap<>();
    private static final int OTP_VALID_DURATION = 3; // in minutes

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void storeOtp(String email, String otp) {
        LocalDateTime expiryTime = LocalDateTime.now().plus(OTP_VALID_DURATION, ChronoUnit.MINUTES);
        otpStorage.put(email, new OTPDetails(otp, expiryTime));
    }

    public boolean verifyOtp(String email, String enteredOtp) {
        OTPDetails otpDetails = otpStorage.get(email);
        if (otpDetails != null && otpDetails.getOtp().equals(enteredOtp)) {
            if (otpDetails.getExpiryTime().isAfter(LocalDateTime.now())) {
                otpStorage.remove(email);
                return true;
            } else {
                otpStorage.remove(email); // Remove expired OTP
            }
        }
        return false;
    }

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void removeExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpStorage.entrySet().removeIf(entry -> entry.getValue().getExpiryTime().isBefore(now));
//        Iterator<Map.Entry<String, OTPDetails>> iterator = otpStorage.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, OTPDetails> entry = iterator.next();
//            if (entry.getValue().getExpiryTime().isBefore(LocalDateTime.now())) {
//              iterator.remove(); // Remove expired OTP
//            }
//        }
    }

    public UserEntity insertUser(UserEntity user) {
        return userRepo.save(user);
    }

    public UserEntity getById(int id) {
        Optional<UserEntity> entityOptional = userRepo.findById(id);
        UserEntity userEntity = new UserEntity();
        if (entityOptional.isPresent()) {
            userEntity = entityOptional.get();
        }
        return userEntity;
    }

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public UserEntity updateUser(UserEntity user) {
        Optional<UserEntity> result = userRepo.findById(user.getId());
        if (result.isPresent()) {
            UserEntity exist = result.get();
            exist.setName(user.getName());
            exist.setEmail(user.getEmail());
            exist.setPassword(user.getPassword());
            exist.setTelegramUsername(user.getTelegramUsername());
            exist.setTelegramUserId(user.getTelegramUserId());
            exist.setImage(user.getImage());
            exist.setRole(user.getRole());
            exist.setIsBanned(user.getIsBanned());
            return userRepo.save(exist);
        }
        // Handle the case when the user does not exist, e.g., throw an exception or return null
        throw new EntityNotFoundException("User not found with ID: " + user.getId());
    }

    public UserEntity findByTelegramUsername(String username){
        return userRepo.findByTelegramUsername(username);
    }

    public String getChatIdByUsername (String username){
        return  userRepo.getChatId(username);
    }



//    public UserEntity authenticate(String email , String password){
//        return userRepo.authenticate(email, password);
//    }


}
