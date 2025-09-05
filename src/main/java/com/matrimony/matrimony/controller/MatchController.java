package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Preferences;
import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.PreferencesRepository;
import com.matrimony.matrimony.repository.PremiumRepository;
import com.matrimony.matrimony.repository.ProfileRepository;
import com.matrimony.matrimony.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Combined MatchController:
 *  - GET /matches         -> percentage-match (0..100) using criteria-count method
 *  - GET /matches/suggested -> weighted score with boosts (premium/recent) returning integer score
 */
@RestController
@RequestMapping("/matches")
public class MatchController {

    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final PreferencesRepository prefRepo;
    private final PremiumRepository premiumRepo;

    public MatchController(UserRepository userRepo,
                           ProfileRepository profileRepo,
                           PreferencesRepository prefRepo,
                           PremiumRepository premiumRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.prefRepo = prefRepo;
        this.premiumRepo = premiumRepo;
    }

    /* -----------------------
       DTOs
       ----------------------- */
    public record MatchResult(Profile profile, double score) {}      // for percentage endpoint
    public record MatchDto(Profile profile, int score) {}            // for suggested endpoint

    /* -----------------------
       Endpoint: percentage-based matching
       GET /matches
       ----------------------- */
    @GetMapping
    public List<MatchResult> findMatches(Authentication auth) {
        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // try findByUser or findByUserId (choose the one your repo exposes)
        Profile myProfile = profileRepo.findByUser(user)
                .orElseGet(() -> profileRepo.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Profile not found")));

        Preferences prefs = prefRepo.findByProfile(myProfile).orElse(null);

        List<Profile> allProfiles = profileRepo.findAll();

        String desiredGender = oppositeGender(myProfile.getGender());

        // if no prefs, return opposite-gender list with score 0
        if (prefs == null) {
            return allProfiles.stream()
                    .filter(p -> !p.getId().equals(myProfile.getId()))
                    .filter(p -> desiredGender.isEmpty() || (p.getGender() != null && p.getGender().equalsIgnoreCase(desiredGender)))
                    .map(p -> {
                        sanitizeUserPassword(p);
                        return new MatchResult(p, 0.0);
                    })
                    .collect(Collectors.toList());
        }

        return allProfiles.stream()
                .filter(p -> !p.getId().equals(myProfile.getId()))
                .filter(p -> desiredGender.isEmpty() || (p.getGender() != null && p.getGender().equalsIgnoreCase(desiredGender)))
                .map(candidate -> new MatchResult(candidate, calculateMatchScorePercentage(candidate, prefs)))
                .filter(mr -> mr.score() >= 20.0) // keep only >= 20%
                .sorted(Comparator.comparingDouble(MatchResult::score).reversed())
                .map(mr -> { sanitizeUserPassword(mr.profile()); return mr; })
                .collect(Collectors.toList());
    }

    /* -----------------------
       Endpoint: weighted suggested matches
       GET /matches/suggested
       ----------------------- */
    @GetMapping("/suggested")
    public List<MatchDto> suggested(Authentication auth) {
        String email = auth.getName();
        User me = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Profile myProfile = profileRepo.findByUser(me)
                .orElseGet(() -> profileRepo.findByUserId(me.getId())
                        .orElseThrow(() -> new RuntimeException("Profile not found")));

        Preferences prefs = prefRepo.findByProfile(myProfile).orElse(null);

        List<Profile> all = profileRepo.findAll();

        String desiredGender = oppositeGender(myProfile.getGender());

        return all.stream()
                .filter(p -> !p.getId().equals(myProfile.getId()))
                .filter(p -> desiredGender.isEmpty() || (p.getGender() != null && p.getGender().equalsIgnoreCase(desiredGender)))
                .map(p -> {
                    double score = calculateWeightedScore(p, prefs);
                    if (isPremium(p.getUser())) score += 4;
                    if (isRecentlyJoined(p)) score += 2;
                    int finalScore = (int) Math.min(100, Math.round(score));
                    sanitizeUserPassword(p);
                    return new MatchDto(p, finalScore);
                })
                .filter(m -> m.score() >= 20) // threshold
                .sorted(Comparator.comparingInt(MatchDto::score).reversed())
                .collect(Collectors.toList());
    }

    /* -----------------------
       Scoring: percentage-style (0..100) based on number of specified criteria
       ----------------------- */
    private double calculateMatchScorePercentage(Profile candidate, Preferences prefs) {
        int totalCriteria = 0;
        int matchedCriteria = 0;

        // Age
        if (prefs.getMinAge() > 0 || prefs.getMaxAge() > 0) {
            totalCriteria++;
            int candidateAge = safeAge(candidate.getDob());
            if (candidateAge >= 0 && candidateAge >= prefs.getMinAge() && candidateAge <= prefs.getMaxAge()) {
                matchedCriteria++;
            }
        }

        // Height
        if (prefs.getMinHeight() > 0 || prefs.getMaxHeight() > 0) {
            totalCriteria++;
            Double candHeight = candidate.getHeight();
            if (candHeight != null && candHeight >= prefs.getMinHeight() && candHeight <= prefs.getMaxHeight()) {
                matchedCriteria++;
            }
        }

        // Religion, Caste, Education, Profession
        if (isSpecified(prefs.getReligion())) { totalCriteria++; if (equalsIgnoreCase(candidate.getReligion(), prefs.getReligion())) matchedCriteria++; }
        if (isSpecified(prefs.getCaste()))    { totalCriteria++; if (equalsIgnoreCase(candidate.getCaste(), prefs.getCaste())) matchedCriteria++; }
        if (isSpecified(prefs.getEducation())){ totalCriteria++; if (equalsIgnoreCase(candidate.getEducation(), prefs.getEducation())) matchedCriteria++; }
        if (isSpecified(prefs.getProfession())){ totalCriteria++; String candProf = candidate.getProfession() != null ? candidate.getProfession() : candidate.getEmploymentType(); if (equalsIgnoreCase(candProf, prefs.getProfession())) matchedCriteria++; }

        // Location
        if (isSpecified(prefs.getCountry())) { totalCriteria++; if (equalsIgnoreCase(candidate.getCountry(), prefs.getCountry())) matchedCriteria++; }
        if (isSpecified(prefs.getState()))   { totalCriteria++; if (equalsIgnoreCase(candidate.getState(), prefs.getState())) matchedCriteria++; }
        if (isSpecified(prefs.getCity()))    { totalCriteria++; if (equalsIgnoreCase(candidate.getCity(), prefs.getCity())) matchedCriteria++; }

        // Lifestyle
        if (isSpecified(prefs.getDiet()))    { totalCriteria++; if (equalsIgnoreCase(candidate.getDiet(), prefs.getDiet())) matchedCriteria++; }
        if (isSpecified(prefs.getSmoking())) { totalCriteria++; if (equalsIgnoreCase(candidate.getSmoking(), prefs.getSmoking())) matchedCriteria++; }
        if (isSpecified(prefs.getDrinking())){ totalCriteria++; if (equalsIgnoreCase(candidate.getDrinking(), prefs.getDrinking())) matchedCriteria++; }

        if (totalCriteria == 0) return 0.0;
        double score = (matchedCriteria * 100.0) / totalCriteria;
        return Math.round(Math.max(0, Math.min(100, score)) * 100.0) / 100.0;
    }

    /* -----------------------
       Scoring: weighted scoring used by /suggested endpoint
       (weights tuned from your example)
       ----------------------- */
    private double calculateWeightedScore(Profile p, Preferences prefs) {
        double score = 0.0;
        if (prefs == null) return 50.0; // fallback when user didn't set preferences

        // Age (max 25)
        if (p.getDob() != null) {
            int age = safeAge(p.getDob());
            if (age >= prefs.getMinAge() && age <= prefs.getMaxAge()) score += 25;
            else {
                if (Math.abs(age - prefs.getMinAge()) <= 3 || Math.abs(age - prefs.getMaxAge()) <= 3) score += 10;
            }
        }

        // Height (max 10)
        if (p.getHeight() != null && prefs.getMinHeight() > 0 && prefs.getMaxHeight() > 0) {
            double h = p.getHeight();
            if (h >= prefs.getMinHeight() && h <= prefs.getMaxHeight()) score += 10;
            else {
                if (Math.abs(h - prefs.getMinHeight()) <= 5 || Math.abs(h - prefs.getMaxHeight()) <= 5) score += 4;
            }
        }

        // Religion (15) / Caste (5)
        if (!isEmpty(prefs.getReligion())) {
            if (equalsIgnoreCase(p.getReligion(), prefs.getReligion())) score += 15;
        } else score += 5;
        if (!isEmpty(prefs.getCaste())) {
            if (equalsIgnoreCase(p.getCaste(), prefs.getCaste())) score += 5;
        } else score += 1;

        // Education (8) / Profession (2)
        if (!isEmpty(prefs.getEducation())) {
            if (equalsIgnoreCase(p.getEducation(), prefs.getEducation())) score += 8;
        } else score += 2;
        if (!isEmpty(prefs.getProfession())) {
            String candProf = p.getEmploymentType() != null ? p.getEmploymentType() : p.getProfession();
            if (equalsIgnoreCase(candProf, prefs.getProfession())) score += 2;
        }

        // Location (country) (5)
        if (!isEmpty(prefs.getCountry())) {
            if (equalsIgnoreCase(p.getCountry(), prefs.getCountry())) score += 5;
        } else score += 1;

        // Lifestyle: diet (6), smoking (3), drinking (3)
        if (!isEmpty(prefs.getDiet())) {
            if (equalsIgnoreCase(p.getDiet(), prefs.getDiet())) score += 6;
        } else score += 1;
        if (!isEmpty(prefs.getSmoking())) {
            if (equalsIgnoreCase(p.getSmoking(), prefs.getSmoking())) score += 3;
        }
        if (!isEmpty(prefs.getDrinking())) {
            if (equalsIgnoreCase(p.getDrinking(), prefs.getDrinking())) score += 3;
        }

        return Math.min(100.0, score);
    }

    /* -----------------------
       Helpers
       ----------------------- */
    private String oppositeGender(String gender) {
        if (gender == null) return "";
        String g = gender.trim().toLowerCase(Locale.ROOT);
        return switch (g) {
            case "male" -> "female";
            case "female" -> "male";
            default -> "";
        };
    }

    private int safeAge(LocalDate dob) {
        if (dob == null) return -1;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    private boolean isRecentlyJoined(Profile p) {
        if (p == null || p.getCreatedDate() == null) return false;
        return p.getCreatedDate().isAfter(LocalDate.now().minusDays(7));
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty() || s.trim().equalsIgnoreCase("any");
    }

    private boolean isSpecified(String s) {
        return !isEmpty(s);
    }

    private void sanitizeUserPassword(Profile p) {
        if (p != null && p.getUser() != null) {
            p.getUser().setPassword(null);
        }
    }

    /**
     * Implement according to your PremiumRepository: return true if this user's premium subscription is active.
     * Example repo methods:
     *   boolean existsByUserIdAndActiveTrue(Long userId);
     * or
     *   Optional<Premium> findActiveByUserId(Long userId);
     */
    private boolean isPremium(User u) {
        if (u == null) return false;
        try {
            // adapt to your repo
            // return premiumRepo.existsByUserIdAndActiveTrue(u.getId());
            return premiumRepo.isActiveForUser(u.getId()); // implement this helper in PremiumRepository
        } catch (Exception ex) {
            return false;
        }
    }
}
