package com.matrimony.matrimony.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Age & height
    private int minAge;          // inclusive
    private int maxAge;          // inclusive
    private double minHeight;    // in cm (or feet), agree on a unit
    private double maxHeight;

    // Faith/community
    private String religion;     // null/empty = no filter
    private String caste;        // null/empty = no filter

    // Education & profession
    private String education;    // e.g., "B.Tech", "MBA"
    private String profession;   // e.g., "Software Engineer" (OPTIONAL)

    // Location preferences – pick either these or keep your single 'location'
    private String country;      // OPTIONAL
    private String state;        // OPTIONAL
    private String city;         // OPTIONAL

    // Lifestyle preferences
    private String diet;         // Veg / Non-Veg / Vegan / Eggetarian (OPTIONAL)
    private String smoking;      // Yes / No / Occasionally (OPTIONAL)
    private String drinking;     // Yes / No / Occasionally (OPTIONAL)

    // Link to the owner’s profile
    @OneToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;
}
