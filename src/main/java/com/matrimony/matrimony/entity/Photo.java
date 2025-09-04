package com.matrimony.matrimony.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")  // ✅ for large images in MySQL
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference   // ✅ Prevent infinite loop
    private Profile profile;

}
