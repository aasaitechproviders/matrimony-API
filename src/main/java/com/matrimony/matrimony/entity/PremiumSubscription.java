package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="premium_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne @JoinColumn(name="user_id") private User user;
    private LocalDateTime expiresAt;
    private boolean active;
}
