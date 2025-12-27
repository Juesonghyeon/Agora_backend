package com.single.agora_backend.controller;

import com.single.agora_backend.dto.Profile.*;
import com.single.agora_backend.dto.ProfileDto;
import com.single.agora_backend.entity.UserProfile;
import com.single.agora_backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 기본 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getProfileInfo(@RequestParam Long userId) {
        try {
            ProfileDto dto = profileService.getProfileInfo(userId);
            if (dto == null) {
                return ResponseEntity.status(404).body("프로필이 존재하지 않습니다.");
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("프로필 정보 가져오기 실패");
        }
    }

    // 친구 목록 조회
    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(@RequestParam Long userId) {
        try {
            List<ProfileDto> friends = profileService.getFriendList(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("친구 목록 가져오기 실패");
        }
    }

    // 프로필 이미지 변경
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam Long userId,
                                         @RequestParam MultipartFile file) {
        try {
            String url = profileService.updateProfileImage(userId, file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("이미지 업로드 실패");
        }
    }

    // 아이디 변경
    @PostMapping("/change-username")
    public ResponseEntity<?> changeUsername(@RequestBody com.single.agora_backend.dto.Profile.ChangeUsernameReq req) {
        try {
            profileService.changeUsername(req.getUserId(), req.getNewUsername());
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("아이디 변경 실패");
        }
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody com.single.agora_backend.dto.Profile.ChangePasswordReq req) {
        try {
            profileService.changePassword(req.getUserId(), req.getOldPassword(), req.getNewPassword());
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("비밀번호 변경 실패");
        }
    }

    // 이메일 추가 및 인증코드 발송
    @PostMapping("/email/send")
    public ResponseEntity<?> addEmail(@RequestBody com.single.agora_backend.dto.Profile.AddEmailReq req) {
        try {
            profileService.addEmail(req.getUserId(), req.getEmail());
            return ResponseEntity.ok("SEND");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("이메일 등록 실패");
        }
    }

    // 이메일 인증
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody com.single.agora_backend.dto.Profile.VerifyEmailReq req) {
        try {
            boolean result = profileService.verifyEmail(req.getUserId(), req.getCode());
            return ResponseEntity.ok(result ? "VERIFIED" : "FAILED");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("이메일 인증 실패");
        }
    }
}
