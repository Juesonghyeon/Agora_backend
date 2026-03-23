package com.single.agora_backend.service;

import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.FriendshipRepository;
import com.single.agora_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor; // 추가 시 생성자 코드를 생략 가능합니다.

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final FriendshipRepository friendshipRepository;

    // 🌟 생성자에서 friendshipRepository도 함께 받아야 빨간 줄이 사라집니다.
    public UserService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.save(new User(username, encodedPassword));
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

    @Transactional // 🌟 친구 관계부터 유저 삭제까지 하나의 작업으로 묶음
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 1. 친구 관계 먼저 삭제 (외래 키 제약 조건 해결)
        friendshipRepository.deleteByRequesterOrReceiver(user, user);

        // 2. 유저 삭제
        userRepository.delete(user);
    }
}