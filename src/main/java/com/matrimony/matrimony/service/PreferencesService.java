package com.matrimony.matrimony.service;

import com.matrimony.matrimony.entity.Preferences;
import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.PreferencesRepository;
import com.matrimony.matrimony.repository.ProfileRepository;
import com.matrimony.matrimony.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public PreferencesService(PreferencesRepository preferencesRepository,
                              ProfileRepository profileRepository,
                              UserRepository userRepository) {
        this.preferencesRepository = preferencesRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public Optional<Preferences> getByUserEmail(String email) {
        return preferencesRepository.findByProfileUserEmail(email);
    }

    public Optional<Preferences> getByUserId(Long userId) {
        return preferencesRepository.findByProfileUserId(userId);
    }

    @Transactional
    public Preferences upsertForUser(String email, Preferences incoming) {
        // Ensure the user & profile exist
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found for user; please create profile first."));

        // Validate ranges
        if (incoming.getMinAge() > incoming.getMaxAge()) {
            throw new IllegalArgumentException("minAge cannot be greater than maxAge");
        }
        if (incoming.getMinHeight() > incoming.getMaxHeight()) {
            throw new IllegalArgumentException("minHeight cannot be greater than maxHeight");
        }

        // Upsert
        Preferences prefs = preferencesRepository.findByProfileUserId(user.getId())
                .orElseGet(() -> Preferences.builder().profile(profile).build());

        // Copy fields (use whatever fields your entity actually has)
        prefs.setMinAge(incoming.getMinAge());
        prefs.setMaxAge(incoming.getMaxAge());
        prefs.setMinHeight(incoming.getMinHeight());
        prefs.setMaxHeight(incoming.getMaxHeight());
        prefs.setReligion(emptyToNull(incoming.getReligion()));
        prefs.setCaste(emptyToNull(incoming.getCaste()));
        prefs.setEducation(emptyToNull(incoming.getEducation()));

        prefs.setProfession(emptyToNull(incoming.getProfession()));  // if you added it
        prefs.setCountry(emptyToNull(incoming.getCountry()));        // if you added it
        prefs.setState(emptyToNull(incoming.getState()));            // if you added it
        prefs.setCity(emptyToNull(incoming.getCity()));              // if you added it

        prefs.setDiet(emptyToNull(incoming.getDiet()));              // if you added it
        prefs.setSmoking(emptyToNull(incoming.getSmoking()));        // if you added it
        prefs.setDrinking(emptyToNull(incoming.getDrinking()));      // if you added it

        return preferencesRepository.save(prefs);
    }

    @Transactional
    public void deleteForUser(String email) {
        preferencesRepository.findByProfileUserEmail(email)
                .ifPresent(preferencesRepository::delete);
    }

    private String emptyToNull(String s) {
        return (s != null && s.trim().isEmpty()) ? null : s;
    }
}
