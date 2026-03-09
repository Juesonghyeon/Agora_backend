package com.single.agora_backend.repository;

import com.single.agora_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String keyword);
}
