package com.single.agora_backend.controller;

import com.single.agora_backend.dto.Profile.*;
import com.single.agora_backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/info")
    public ResponseEntity<ProfileDto> getInfo(@RequestParam Long userId) {
        return ResponseEntity.ok(profileService.getProfileInfo(userId));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam Long userId, @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(profileService.updateProfileImage(userId, file));
    }

    @GetMapping("/friends/all")
    public ResponseEntity<Map<String, List<FriendDto>>> getFriends(@RequestParam Long userId) {
        return ResponseEntity.ok(profileService.getFriendData(userId));
    }

    // [추가됨] 친구 요청 API
    @PostMapping("/friends/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequestDto req) {
        try {
            profileService.sendFriendRequest(req.getUserId(), req.getTargetId());
            return ResponseEntity.ok("친구 요청 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/friends/respond")
    public ResponseEntity<Void> respond(@RequestBody Map<String, Object> payload) {
        Long friendshipId = Long.valueOf(payload.get("friendshipId").toString());
        boolean accept = (boolean) payload.get("accept");
        profileService.respondRequest(friendshipId, accept);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest req) {
        profileService.changePassword(req);
        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    @PostMapping("/change-username")
    public ResponseEntity<String> changeUsername(@RequestBody UsernameChangeRequest req) {
        profileService.changeUsername(req);
        return ResponseEntity.ok("아이디 변경 성공");
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchUserDto>> searchUsers(@RequestParam Long userId, @RequestParam String keyword) {
        return ResponseEntity.ok(profileService.searchUsers(userId, keyword));
    }

    @PostMapping("/email/request")
    public ResponseEntity<Void> requestEmail(@RequestBody EmailRequest req) {
        profileService.sendVerificationEmail(req.getUserId(), req.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody EmailVerifyRequest req) {
        profileService.verifyEmailCode(req);
        return ResponseEntity.ok().build();
    }
}