package com.single.agora_backend.service;

import com.single.agora_backend.dto.ProfileDto;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.entity.UserProfile;
import com.single.agora_backend.repository.UserProfileRepository;
import com.single.agora_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final BCryptPasswordEncoder passwordEncoder; // DI 주입

    // 프로필 정보 조회
    public ProfileDto getProfileInfo(Long userId) {
        UserProfile profile = profileRepository.findByUser_Id(userId);
        if (profile == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            profile = new UserProfile();
            profile.setUser(user);
            profile.setEmailVerified(false);
            profile = profileRepository.save(profile);
        }

        return new ProfileDto(
                profile.getUser().getId(),
                profile.getUser().getUsername(),
                profile.getEmail(),
                profile.getProfileImageUrl(),
                profile.isEmailVerified()
        );
    }

    public List<ProfileDto> getFriendList(Long userId) {
        return Collections.emptyList();
    }

    // 프로필 이미지 업로드 (실제 파일 저장 + DB URL)
    public String updateProfileImage(Long userId, MultipartFile file) throws IOException {
        UserProfile profile = profileRepository.findByUser_Id(userId);
        if (profile == null) throw new RuntimeException("Profile not found");

        String uploadDir = "uploads/profiles/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String ext = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + ext;
        File dest = new File(dir, filename);
        file.transferTo(dest);

        String fileUrl = "/uploads/profiles/" + filename;
        profile.setProfileImageUrl(fileUrl);
        profileRepository.save(profile);

        return fileUrl;
    }

    // 아이디 변경
    public void changeUsername(Long userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userRepository.findByUsername(newUsername).isPresent())
            throw new RuntimeException("Username already exists.");
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    // 비밀번호 변경
    public void changePassword(Long userId, String oldPw, String newPw) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPw, user.getPassword()))
            throw new RuntimeException("Old password incorrect");
        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }

    // 이메일 등록 + 인증코드 발송
    public void addEmail(Long userId, String email) {
        UserProfile profile = profileRepository.findByUser_Id(userId);
        if (profile == null) throw new RuntimeException("Profile not found");

        profile.setEmail(email);
        profile.setEmailVerified(false);
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        profile.setVerificationCode(code);
        System.out.println("EMAIL CODE for " + email + " = " + code); // 테스트용
        profileRepository.save(profile);
    }

    // 이메일 인증
    public boolean verifyEmail(Long userId, String code) {
        UserProfile profile = profileRepository.findByUser_Id(userId);
        if (profile == null) return false;

        if (profile.getVerificationCode() != null && profile.getVerificationCode().equals(code)) {
            profile.setEmailVerified(true);
            profile.setVerificationCode(null);
            profileRepository.save(profile);
            return true;
        }
        return false;
    }
}
