package com.matrimony.matrimony.repository;


import com.matrimony.matrimony.entity.Preferences;
import com.matrimony.matrimony.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
    Optional<Preferences> findByProfileUserId(Long userId);
    Optional<Preferences> findByProfileUserEmail(String email);
    Optional<Preferences> findByProfile(Profile profile);
}
