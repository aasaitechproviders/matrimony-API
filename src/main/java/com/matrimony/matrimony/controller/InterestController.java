package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Chat;
import com.matrimony.matrimony.entity.Interest;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.ChatRepository;
import com.matrimony.matrimony.repository.InterestRepository;
import com.matrimony.matrimony.repository.ProfileRepository;
import com.matrimony.matrimony.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/interests")
public class InterestController {
    private final InterestRepository interestRepo;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final ChatRepository chatRepo;

    public InterestController(InterestRepository interestRepo, UserRepository userRepo,
                              ProfileRepository profileRepo, ChatRepository chatRepo) {
        this.interestRepo = interestRepo;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.chatRepo = chatRepo;
    }

    @PostMapping("/{toUserId}")
    public ResponseEntity<?> sendInterest(Authentication auth, @PathVariable Long toUserId) {
        User from = userRepo.findByEmail(auth.getName()).orElseThrow();
        User to = userRepo.findById(toUserId).orElseThrow();

        // prevent duplicates
        interestRepo.findByFromUserAndToUser(from, to).ifPresent(i -> {
            throw new RuntimeException("Interest already sent");
        });

        Interest i = Interest.builder()
                .fromUser(from).toUser(to).status(Interest.Status.PENDING).createdAt(LocalDateTime.now()).build();
        interestRepo.save(i);

        // if reciprocal interest exists and is PENDING -> auto accept both -> create chat
        interestRepo.findByFromUserAndToUser(to, from).ifPresent(recip -> {
            if (recip.getStatus() == Interest.Status.PENDING) {
                recip.setStatus(Interest.Status.ACCEPTED);
                interestRepo.save(recip);
                i.setStatus(Interest.Status.ACCEPTED);
                interestRepo.save(i);

                // create chat
                Chat chat = Chat.builder().userA(from).userB(to).active(true).createdAt(LocalDateTime.now()).build();
                chatRepo.save(chat);
            }
        });

        return ResponseEntity.ok("Interest sent");
    }

    @PostMapping("/{interestId}/respond")
    public ResponseEntity<?> respond(@PathVariable Long interestId, @RequestParam String action) {
        Interest interest = interestRepo.findById(interestId).orElseThrow();
        if (action.equalsIgnoreCase("accept")) {
            interest.setStatus(Interest.Status.ACCEPTED);
            interestRepo.save(interest);
            // create chat
            Chat chat = Chat.builder().userA(interest.getFromUser()).userB(interest.getToUser()).active(true).createdAt(LocalDateTime.now()).build();
            chatRepo.save(chat);
        } else if (action.equalsIgnoreCase("reject")) {
            interest.setStatus(Interest.Status.REJECTED);
            interestRepo.save(interest);
        }
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/incoming")
    public List<Interest> incoming(Authentication auth) {
        User me = userRepo.findByEmail(auth.getName()).orElseThrow();
        return interestRepo.findByToUserAndStatus(me, Interest.Status.PENDING);
    }
}
