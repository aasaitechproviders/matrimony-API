package com.matrimony.matrimony.repository;

import com.matrimony.matrimony.entity.Interest;
import com.matrimony.matrimony.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    Optional<Interest> findByFromUserAndToUser(User fromUser, User toUser);

    List<Interest> findByToUser(User toUser);

    List<Interest> findByFromUser(User fromUser);

    // ✅ Add this to fix your error
    List<Interest> findByToUserAndStatus(User toUser, Interest.Status status);

    // (Optional) also useful:
    List<Interest> findByFromUserAndStatus(User fromUser, Interest.Status status);
}
