package com.single.agora_backend.repository;

import com.single.agora_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUser_Id(Long userId);
}