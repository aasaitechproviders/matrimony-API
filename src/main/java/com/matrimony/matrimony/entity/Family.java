package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "families")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fatherOccupation;
    private String motherOccupation;
    private int siblingsCount;
    private String nativePlace;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
