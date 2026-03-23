package com.single.agora_backend.controller;

import com.single.agora_backend.entity.User;
import com.single.agora_backend.entity.UserProfile;
import com.single.agora_backend.repository.UserProfileRepository;
import com.single.agora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/find-username")
    public ResponseEntity<String> findUsername(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        UserProfile profile = userProfileRepository.findByEmail(email);

        if (profile == null || !profile.isEmailVerified()) {
            return ResponseEntity.badRequest().body("가입되지 않거나 인증되지 않은 계정입니다.");
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        profile.setVerificationCode(code);
        userProfileRepository.save(profile);

        sendMail(email, "[Agora] 아이디 찾기 인증번호", "인증번호: " + code);
        return ResponseEntity.ok("인증번호 전송 완료");
    }

    @PostMapping("/verify-id-code")
    public ResponseEntity<String> verifyIdCode(@RequestBody Map<String, String> payload) {
        UserProfile profile = userProfileRepository.findByEmail(payload.get("email"));
        if (profile != null && payload.get("code").equals(profile.getVerificationCode())) {
            profile.setVerificationCode(null);
            userProfileRepository.save(profile);
            return ResponseEntity.ok("아이디: [" + profile.getUser().getUsername() + "]");
        }
        return ResponseEntity.badRequest().body("인증번호 불일치");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        UserProfile profile = userProfileRepository.findByEmail(payload.get("email"));
        if (profile == null || !profile.getUser().getUsername().equals(payload.get("username"))) {
            return ResponseEntity.badRequest().body("정보가 일치하지 않습니다.");
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        profile.setVerificationCode(code);
        userProfileRepository.save(profile);

        sendMail(profile.getEmail(), "[Agora] 비밀번호 재설정 인증번호", "인증번호: " + code);
        return ResponseEntity.ok("인증번호 전송 완료");
    }

    @PostMapping("/verify-pw-code")
    public ResponseEntity<String> verifyPwCode(@RequestBody Map<String, String> payload) {
        UserProfile profile = userProfileRepository.findByEmail(payload.get("email"));
        if (profile != null && payload.get("code").equals(profile.getVerificationCode())) {
            String tempPw = UUID.randomUUID().toString().substring(0, 8) + "!";
            User user = profile.getUser();
            user.setPassword(passwordEncoder.encode(tempPw));
            userRepository.save(user);

            profile.setVerificationCode(null);
            userProfileRepository.save(profile);

            sendMail(profile.getEmail(), "[Agora] 임시 비밀번호 안내", "임시 비밀번호: " + tempPw + "\n로그인 후 비밀번호를 변경해주세요.");
            return ResponseEntity.ok("임시 비밀번호가 전송되었습니다.");
        }
        return ResponseEntity.badRequest().body("인증번호 불일치");
    }

    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}