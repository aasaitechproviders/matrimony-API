package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minAge;
    private int maxAge;
    private double minHeight;
    private double maxHeight;
    private String religion;
    private String caste;
    private String education;
    private String location;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
