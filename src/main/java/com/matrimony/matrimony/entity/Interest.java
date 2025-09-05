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

    // who expressed interest
    @ManyToOne(optional = false) @JoinColumn(name = "from_user_id")
    private User fromUser;

    // who received interest (profile owner)
    @ManyToOne(optional = false) @JoinColumn(name = "to_user_id")
    private User toUser;

    @Enumerated(EnumType.STRING)
    private Status status; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime createdAt;

    public enum Status { PENDING, ACCEPTED, REJECTED }
}
