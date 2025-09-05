package com.matrimony.matrimony.repository;

import com.matrimony.matrimony.entity.Profile;
import com.matrimony.matrimony.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
    Optional<Profile> findByUserEmail(String email);
    Optional<Profile> findByUser(User user);

}
