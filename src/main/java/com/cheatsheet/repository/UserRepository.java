package com.cheatsheet.repository;

import com.cheatsheet.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("select u from UserEntity u where u.email=?1")
    UserEntity authenticate(String email);

    Optional<UserEntity> findByEmail(String email);

    @Query ("select u from UserEntity u where u.telegramUsername=?1")
    UserEntity findByTelegramUsername(String name);

    @Query ("select u.telegramUserId from UserEntity u where u.telegramUsername=?1")
    String getChatId(String username);
}
