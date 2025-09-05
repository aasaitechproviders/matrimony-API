package com.matrimony.matrimony.repository;

import com.matrimony.matrimony.entity.Chat;
import com.matrimony.matrimony.entity.Family;
import com.matrimony.matrimony.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserAOrUserB(User userA, User userB);
}
