package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.repository.ProfileRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final ProfileRepository profileRepo;

    public SearchController(ProfileRepository profileRepo) { this.profileRepo = profileRepo; }

    @GetMapping
    public List<Profile> search(
            @RequestParam(required=false) String religion,
            @RequestParam(required=false) String caste,
            @RequestParam(required=false) String education,
            @RequestParam(required=false) String country,
            @RequestParam(required=false) Double minIncome,
            @RequestParam(required=false) Double maxIncome
    ) {
        // naive approach: fetch all and filter in memory OR create a JPA Specification for better performance
        List<Profile> all = profileRepo.findAll();
        return all.stream()
                .filter(p -> religion == null || religion.isBlank() ||
                        p.getReligion() != null && p.getReligion().equalsIgnoreCase(religion
                        ))
                .filter(p -> caste == null || caste.isBlank() ||
                        p.getCaste() != null && p.getCaste().equalsIgnoreCase(caste))
                .filter(p -> education == null || education.isBlank() ||
                        p.getEducation()!=null && p.getEducation().equalsIgnoreCase(education))
                .filter(p -> country == null || country.isBlank() ||
                        (p.getCountry()!=null && p.getCountry().equalsIgnoreCase(country)))
                .filter(p -> minIncome == null || p.getIncome()!=null && p.getIncome() >= minIncome)
                .filter(p -> maxIncome == null || p.getIncome()!=null && p.getIncome() <= maxIncome)
                .toList();
    }
}
