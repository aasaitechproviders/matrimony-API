package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Chat;
import com.matrimony.matrimony.entity.Message;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.ChatRepository;
import com.matrimony.matrimony.repository.MessageRepository;
import com.matrimony.matrimony.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatRepository chatRepo;
    private final MessageRepository msgRepo;
    private final UserRepository userRepo;

    public ChatController(ChatRepository chatRepo, MessageRepository msgRepo, UserRepository userRepo) {
        this.chatRepo = chatRepo; this.msgRepo = msgRepo; this.userRepo = userRepo;
    }

    @GetMapping
    public List<Chat> myChats(Authentication auth) {
        User me = userRepo.findByEmail(auth.getName()).orElseThrow();
        return chatRepo.findByUserAOrUserB(me, me);
    }

    @GetMapping("/{chatId}/messages")
    public List<Message> getMessages(Authentication auth, @PathVariable Long chatId) {
        // authorize membership
        Chat chat = chatRepo.findById(chatId).orElseThrow();
        // TODO: check auth user is participant
        return msgRepo.findByChatOrderBySentAtAsc(chat);
    }

    @PostMapping("/{chatId}/send")
    public Message sendMessage(Authentication auth, @PathVariable Long chatId, @RequestBody Map<String,String> body) {
        User me = userRepo.findByEmail(auth.getName()).orElseThrow();
        Chat chat = chatRepo.findById(chatId).orElseThrow();
        // check membership
        Message m = Message.builder().chat(chat).fromUser(me).content(body.get("content")).sentAt(LocalDateTime.now()).is_read(false).build();
        return msgRepo.save(m);
    }
}

