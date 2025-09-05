package com.matrimony.matrimony.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Basic Info
    private String fullName;
    private String gender;
    private LocalDate dob;
    private String email;        // ✅ user email (duplicate for quick view)

    // 🔹 Cultural Info
    private String religion;
    private String caste;
    private String motherTongue;
    private String maritalStatus;
    private String kulatheivam;

    // 🔹 Astrology Info
    private String raasi;        // dropdown (predefined values)
    private String natchathiram; // dropdown (predefined values)
    private String laknam;       // dropdown (predefined values)
    private String bloodGroup;

    // 🔹 Education / Profession
    private String education;
    private String profession;
    private String employmentType;   // Employed / Self-Employed
    private String companyName;
    private String companyAddress;
    private Double income;

    // 🔹 Family Info
    private String fatherName;
    private String motherName;
    private String fatherContact;
    private String motherContact;
    private String fatherOccupation;
    private String motherOccupation;
    private String fatherNativePlace;
    private String motherNativePlace;
    private Double familyAnnualIncome;

    // 🔹 Siblings
    private String brotherDetails;   // JSON/Comma-separated name, age, marital status
    private String sisterDetails;    // same as above

    // 🔹 Physical Attributes
    private Double height;
    private Double weight;
    private String complexion;   // fair / wheatish / dark
    private String bodyType;     // slim / average / athletic / heavy

    // 🔹 Lifestyle
    private String diet;     // Veg / Non-Veg / Vegan / Eggetarian
    private String smoking;  // Yes / No
    private String drinking; // Yes / No
    // Location preferences – pick either these or keep your single 'location'
    private String country;      // OPTIONAL
    private String state;        // OPTIONAL
    private String city;         // OPTIONAL
    // 🔹 Additional Info
    private String birthTime;    // HH:mm format
    private String birthPlace;   // city/town/village
    private String address;      // full address
    private String hobbies;      // comma-separated hobbies

    private LocalDate createdDate; // when profile was created

    // 🔹 Photos
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Photo> photos = new ArrayList<>();

    // 🔹 Relationship with User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
