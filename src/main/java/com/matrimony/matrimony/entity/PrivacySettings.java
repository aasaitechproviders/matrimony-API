package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="privacy_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivacySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne @JoinColumn(name="user_id", unique=true) private User user;

    // who can see contacts: EVERYONE, MATCHES_ONLY, NO_ONE
    @Enumerated(EnumType.STRING)
    private ContactVisibility contactVisibility = ContactVisibility.MATCHES_ONLY;

    private boolean photosVisible = true;
    private boolean emailVisible = false;

    public enum ContactVisibility { EVERYONE, MATCHES_ONLY, NO_ONE }
}
