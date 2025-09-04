package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // sender user
    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    // receiver user
    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    private String status = "PENDING"; // PENDING / ACCEPTED / REJECTED
    private LocalDateTime createdAt = LocalDateTime.now();
}
