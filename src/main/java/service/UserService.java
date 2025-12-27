package com.single.agora_backend.service;

import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword);
        return userRepository.save(user);
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        return passwordEncoder.matches(password, userOpt.get().getPassword());
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public void updateUsername(Long id, String newUsername) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void updatePassword(Long id, String currentPw, String newPw) {
        User user = userRepository.findById(id).orElseThrow();
        if (!passwordEncoder.matches(currentPw, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호 불일치");
        }
        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
