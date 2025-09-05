package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the two participants (assuming 1:1 chat)
    @ManyToOne @JoinColumn(name="user_a") private User userA;
    @ManyToOne @JoinColumn(name="user_b") private User userB;

    private boolean active;
    private LocalDateTime createdAt;
}


