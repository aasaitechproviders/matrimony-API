package com.matrimony.matrimony.repository;

import com.matrimony.matrimony.entity.Chat;
import com.matrimony.matrimony.entity.Interest;
import com.matrimony.matrimony.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderBySentAtAsc(Chat chat);
}
