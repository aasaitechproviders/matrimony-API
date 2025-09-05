package com.matrimony.matrimony.repository;

import com.matrimony.matrimony.entity.PremiumSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiumRepository extends JpaRepository<PremiumSubscription, Long> {
    PremiumSubscription findByUserId(Long userId);
    // ✅ Checks if a user has an active premium subscription
    boolean existsByUserIdAndActiveTrue(Long userId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM PremiumSubscription p " +
            "WHERE p.user.id = :userId AND p.active = true")
    boolean isActiveForUser(@Param("userId") Long userId);
}
