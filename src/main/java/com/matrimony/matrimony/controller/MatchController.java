package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Preferences;
import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.PreferencesRepository;
import com.matrimony.matrimony.repository.ProfileRepository;
import com.matrimony.matrimony.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PreferencesRepository preferencesRepository;

    public MatchController(UserRepository userRepository,
                           ProfileRepository profileRepository,
                           PreferencesRepository preferencesRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.preferencesRepository = preferencesRepository;
    }

    // --- Helper: compute age from DOB safely
    private int calculateAge(LocalDate dob) {
        if (dob == null) return -1;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    // --- DTO to return profile + score
    public static record MatchResult(Profile profile, double score) {}

    @GetMapping
    public List<MatchResult> findMatches(Authentication auth) {
        String email = auth.getName();

        // 1) Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) Find logged-in user's profile (use findByUserId)
        Profile myProfile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 3) Find preferences for this profile; if none, return empty list or default
        Preferences prefs = preferencesRepository.findByProfile(myProfile)
                .orElse(null);

        // 4) Get all other profiles
        List<Profile> allProfiles = profileRepository.findAll();

        // 5) Determine desired gender (opposite of logged-in user's gender)
        String myGender = myProfile.getGender() == null ? "" : myProfile.getGender().trim();
        String desiredGender = switch (myGender.toLowerCase(Locale.ROOT)) {
            case "male" -> "female";
            case "female" -> "male";
            default -> ""; // empty => no gender filter (you can choose default behavior)
        };

        // 6) If no preferences set, return default empty or all opposite gender sorted by some basic rule.
        if (prefs == null) {
            return allProfiles.stream()
                    .filter(p -> !p.getId().equals(myProfile.getId()))
                    .filter(p -> desiredGender.isEmpty() || (p.getGender() != null && p.getGender().equalsIgnoreCase(desiredGender)))
                    .map(p -> new MatchResult(p, 0.0))
                    .collect(Collectors.toList());
        }

        // 7) Compute score for each candidate, filter and sort
        List<MatchResult> results = allProfiles.stream()
                .filter(p -> !p.getId().equals(myProfile.getId())) // skip self
                .filter(p -> {
                    // filter by opposite gender if desiredGender is set
                    if (!desiredGender.isBlank()) {
                        return p.getGender() != null && p.getGender().equalsIgnoreCase(desiredGender);
                    }
                    return true;
                })
                .map(candidate -> new MatchResult(candidate, calculateMatchScore(candidate, prefs)))
                // only include scoring >= 20% (adjust threshold here if you want)
                .filter(mr -> mr.score() >= 20.0)
                .sorted(Comparator.comparingDouble(MatchResult::score).reversed())
                .collect(Collectors.toList());

        return results;
    }

    /**
     * Compute a match score (0..100) between candidate profile and preferences.
     * Only preference fields that are specified (non-null, non-empty, not "Any")
     * count toward the total number of criteria.
     */
    private double calculateMatchScore(Profile candidate, Preferences prefs) {
        int totalCriteria = 0;
        int matchedCriteria = 0;

        // --- Age
        if (prefs.getMinAge() > 0 || prefs.getMaxAge() > 0) {
            totalCriteria++;
            int candidateAge = calculateAge(candidate.getDob());
            if (candidateAge >= 0 &&
                    candidateAge >= prefs.getMinAge() && candidateAge <= prefs.getMaxAge()) {
                matchedCriteria++;
            }
        }

        // --- Height
        if (prefs.getMinHeight() > 0 || prefs.getMaxHeight() > 0) {
            totalCriteria++;
            Double candHeight = candidate.getHeight();
            if (candHeight != null &&
                    candHeight >= prefs.getMinHeight() &&
                    candHeight <= prefs.getMaxHeight()) {
                matchedCriteria++;
            }
        }

        // --- Religion
        if (isSpecified(prefs.getReligion())) {
            totalCriteria++;
            if (candidate.getReligion() != null && candidate.getReligion().equalsIgnoreCase(prefs.getReligion())) {
                matchedCriteria++;
            }
        }

        // --- Caste
        if (isSpecified(prefs.getCaste())) {
            totalCriteria++;
            if (candidate.getCaste() != null && candidate.getCaste().equalsIgnoreCase(prefs.getCaste())) {
                matchedCriteria++;
            }
        }

        // --- Education
        if (isSpecified(prefs.getEducation())) {
            totalCriteria++;
            if (candidate.getEducation() != null && candidate.getEducation().equalsIgnoreCase(prefs.getEducation())) {
                matchedCriteria++;
            }
        }

        // --- Profession
        if (isSpecified(prefs.getProfession())) {
            totalCriteria++;
            // Note: candidate may use employmentType or profession - adjust accordingly
            String candProf = candidate.getProfession() != null ? candidate.getProfession() : candidate.getEmploymentType();
            if (candProf != null && candProf.equalsIgnoreCase(prefs.getProfession())) {
                matchedCriteria++;
            }
        }

        // --- Location: country/state/city (each counts if provided)
        if (isSpecified(prefs.getCountry())) {
            totalCriteria++;
            if (candidate.getCountry() != null && candidate.getCountry().equalsIgnoreCase(prefs.getCountry())) {
                matchedCriteria++;
            }
        }
        if (isSpecified(prefs.getState())) {
            totalCriteria++;
            if (candidate.getState() != null && candidate.getState().equalsIgnoreCase(prefs.getState())) {
                matchedCriteria++;
            }
        }
        if (isSpecified(prefs.getCity())) {
            totalCriteria++;
            if (candidate.getCity() != null && candidate.getCity().equalsIgnoreCase(prefs.getCity())) {
                matchedCriteria++;
            }
        }

        // --- Lifestyle: diet
        if (isSpecified(prefs.getDiet())) {
            totalCriteria++;
            if (candidate.getDiet() != null && candidate.getDiet().equalsIgnoreCase(prefs.getDiet())) {
                matchedCriteria++;
            }
        }

        // --- Lifestyle: smoking
        if (isSpecified(prefs.getSmoking())) {
            totalCriteria++;
            if (candidate.getSmoking() != null && candidate.getSmoking().equalsIgnoreCase(prefs.getSmoking())) {
                matchedCriteria++;
            }
        }

        // --- Lifestyle: drinking
        if (isSpecified(prefs.getDrinking())) {
            totalCriteria++;
            if (candidate.getDrinking() != null && candidate.getDrinking().equalsIgnoreCase(prefs.getDrinking())) {
                matchedCriteria++;
            }
        }

        // If no criteria were defined, return 0
        if (totalCriteria == 0) {
            return 0.0;
        }

        // compute percentage
        double score = (matchedCriteria * 100.0) / totalCriteria;
        // clamp and round
        if (score < 0) score = 0;
        if (score > 100) score = 100;
        return Math.round(score * 100.0) / 100.0; // round to 2 decimals
    }

    // Helper: treat null/empty/"any" as unspecified
    private boolean isSpecified(String s) {
        if (s == null) return false;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return false;
        return !trimmed.equalsIgnoreCase("any");
    }
}
