package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="chat_id") private Chat chat;
    @ManyToOne @JoinColumn(name="from_user") private User fromUser;
    private String content;
    private LocalDateTime sentAt;
    private boolean is_read;
}