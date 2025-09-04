package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Photo;
import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.PhotoRepository;
import com.matrimony.matrimony.repository.UserRepository;
import com.matrimony.matrimony.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;
    @Autowired
    PhotoRepository photoRepository;
    public ProfileController(ProfileService profileService, UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }
    @GetMapping("/byEmail")
    public ResponseEntity<Profile> getProfileByEmail(Authentication auth) {
        String email = auth.getName();  // JWT subject is email
        return profileService.getProfileByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Profile> createOrUpdateProfile(
            @RequestPart("profile") Profile profileData,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication auth) throws IOException {

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile savedProfile = profileService.createOrUpdateProfile(user.getId(), profileData, photos);
        return ResponseEntity.ok(savedProfile);
    }



    // ✅ Get Profile
    @GetMapping
    public ResponseEntity<Profile> getProfile(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return profileService.getProfile(user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete Profile
    @DeleteMapping
    public ResponseEntity<String> deleteProfile(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileService.deleteProfile(user.getId());
        return ResponseEntity.ok("Profile deleted successfully!");
    }
    @GetMapping("/photos/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getData());
    }


}
