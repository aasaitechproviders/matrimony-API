package com.matrimony.matrimony.repository;

import com.matrimony.matrimony.entity.Photo;
import com.matrimony.matrimony.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
