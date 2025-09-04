package com.matrimony.matrimony.repository;


import com.matrimony.matrimony.entity.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferencesRepository extends
        JpaRepository<Preferences, Long> {
}
