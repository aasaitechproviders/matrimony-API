package com.matrimony.matrimony.service;
import com.matrimony.matrimony.entity.Photo;
import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.ProfileRepository;
import com.matrimony.matrimony.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Profile createOrUpdateProfile(Long userId, Profile profileData, List<MultipartFile> photos) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Profile> existingProfileOpt = profileRepository.findByUserId(userId);

        Profile profile = existingProfileOpt.orElse(new Profile());
        profile.setUser(user);

        // 🔹 Basic Info
        profile.setFullName(profileData.getFullName());
        profile.setGender(profileData.getGender());
        profile.setDob(profileData.getDob());
        profile.setEmail(profileData.getEmail());

        // 🔹 Cultural Info
        profile.setReligion(profileData.getReligion());
        profile.setCaste(profileData.getCaste());
        profile.setMotherTongue(profileData.getMotherTongue());
        profile.setMaritalStatus(profileData.getMaritalStatus());
        profile.setKulatheivam(profileData.getKulatheivam());

        // 🔹 Astrology Info
        profile.setRaasi(profileData.getRaasi());
        profile.setNatchathiram(profileData.getNatchathiram());
        profile.setLaknam(profileData.getLaknam());
        profile.setBloodGroup(profileData.getBloodGroup());

        // 🔹 Education / Profession
        profile.setEducation(profileData.getEducation());
        profile.setProfession(profileData.getProfession());
        profile.setEmploymentType(profileData.getEmploymentType());
        profile.setCompanyName(profileData.getCompanyName());
        profile.setCompanyAddress(profileData.getCompanyAddress());
        profile.setIncome(profileData.getIncome());

        // 🔹 Family Info
        profile.setFatherName(profileData.getFatherName());
        profile.setMotherName(profileData.getMotherName());
        profile.setFatherContact(profileData.getFatherContact());
        profile.setMotherContact(profileData.getMotherContact());
        profile.setFatherOccupation(profileData.getFatherOccupation());
        profile.setMotherOccupation(profileData.getMotherOccupation());
        profile.setFatherNativePlace(profileData.getFatherNativePlace());
        profile.setMotherNativePlace(profileData.getMotherNativePlace());
        profile.setFamilyAnnualIncome(profileData.getFamilyAnnualIncome());

        // 🔹 Siblings
        profile.setBrotherDetails(profileData.getBrotherDetails());
        profile.setSisterDetails(profileData.getSisterDetails());

        // 🔹 Physical Attributes
        profile.setHeight(profileData.getHeight());
        profile.setWeight(profileData.getWeight());
        profile.setComplexion(profileData.getComplexion());
        profile.setBodyType(profileData.getBodyType());

        // 🔹 Lifestyle
        profile.setDiet(profileData.getDiet());
        profile.setSmoking(profileData.getSmoking());
        profile.setDrinking(profileData.getDrinking());

        // 🔹 Additional Info
        profile.setBirthTime(profileData.getBirthTime());
        profile.setBirthPlace(profileData.getBirthPlace());
        profile.setAddress(profileData.getAddress());
        profile.setHobbies(profileData.getHobbies());
        profile.setCreatedDate(profileData.getCreatedDate());

        // ✅ Handle new photos if provided
        if (photos != null && !photos.isEmpty()) {
            List<Photo> photoEntities = new ArrayList<>();

            for (MultipartFile file : photos) {
                Photo photo = Photo.builder()
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes()) // store as BLOB
                        .profile(profile)
                        .build();
                photoEntities.add(photo);
            }

            // merge with existing photos
            if (profile.getPhotos() != null) {
                profile.getPhotos().addAll(photoEntities);
            } else {
                profile.setPhotos(photoEntities);
            }
        }

        return profileRepository.save(profile);
    }

    public Optional<Profile> getProfile(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    public void deleteProfile(Long userId) {
        profileRepository.findByUserId(userId).ifPresent(profileRepository::delete);
    }

    public Optional<Profile> getProfileByEmail(String email) {
        return profileRepository.findByUserEmail(email);
    }
    public Profile getProfileByUser(User user) {
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    // Create a blank profile if not exists
                    Profile newProfile = new Profile();
                    newProfile.setUser(user);
                    newProfile.setEmail(user.getEmail());
                    return profileRepository.save(newProfile);
                });
    }


}