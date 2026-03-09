package com.single.agora_backend.service;

import com.single.agora_backend.dto.Profile.*;
import com.single.agora_backend.entity.*;
import com.single.agora_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final FriendshipRepository friendshipRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // [수정됨] 파일명을 깔끔하게 하나로 고정했습니다. 이 경로에 원하시는 기본 이미지를 위치시켜주세요.
    private final String DEFAULT_IMAGE = "/uploads/profiles/default.jpg";

    public ProfileDto getProfileInfo(Long userId) {
        UserProfile profile = profileRepository.findByUser_Id(userId);

        if (profile == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            profile = new UserProfile();
            profile.setUser(user);
            profile.setProfileImageUrl(DEFAULT_IMAGE);
            profile.setEmailVerified(false);
            profileRepository.save(profile);
        }else if (profile.getProfileImageUrl() == null || profile.getProfileImageUrl().isBlank() || profile.getProfileImageUrl().equals("/uploads/profiles/default.jpg")) {
            // [핵심 수정] 이미 프로필은 있지만 이미지 경로가 비어있거나 구버전일 경우 보정
            profile.setProfileImageUrl(DEFAULT_IMAGE);
            profileRepository.save(profile);
        }

        User user = profile.getUser();
        return new ProfileDto(user.getId(), user.getUsername(), profile.getEmail(),
                profile.getProfileImageUrl(), profile.isEmailVerified());
    }

    public String updateProfileImage(Long userId, MultipartFile file) throws IOException {
        String uploadDir = "C:/agora_uploads/profiles/";
        String fileName = userId + "_" + java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
        File saveFile = new File(uploadDir + fileName);

        if (!saveFile.getParentFile().exists()) saveFile.getParentFile().mkdirs();
        file.transferTo(saveFile);

        UserProfile profile = profileRepository.findByUser_Id(userId);
        if (profile == null) {
            User user = userRepository.findById(userId).orElseThrow();
            profile = new UserProfile();
            profile.setUser(user);
        }

        String url = "/uploads/profiles/" + fileName;
        profile.setProfileImageUrl(url);
        profileRepository.save(profile);
        return url;
    }

    public Map<String, List<FriendDto>> getFriendData(Long userId) {
        List<FriendDto> friends = friendshipRepository.findAcceptedFriendsByUserId(userId).stream()
                .map(f -> {
                    User target = f.getRequester().getId().equals(userId) ? f.getReceiver() : f.getRequester();
                    UserProfile targetProfile = profileRepository.findByUser_Id(target.getId());
                    String img = (targetProfile != null && targetProfile.getProfileImageUrl() != null) ? targetProfile.getProfileImageUrl() : DEFAULT_IMAGE;
                    return new FriendDto(f.getId(), target.getId(), target.getUsername(), img, target.isOnline());
                }).toList();

        List<FriendDto> received = friendshipRepository.findByReceiverIdAndStatus(userId, Friendship.FriendStatus.PENDING).stream()
                .map(f -> {
                    UserProfile reqProfile = profileRepository.findByUser_Id(f.getRequester().getId());
                    String img = (reqProfile != null && reqProfile.getProfileImageUrl() != null) ? reqProfile.getProfileImageUrl() : DEFAULT_IMAGE;
                    return new FriendDto(f.getId(), f.getRequester().getId(), f.getRequester().getUsername(), img, false);
                }).toList();

        return Map.of("friends", friends, "received", received);
    }

    // [추가됨] 친구 요청 비즈니스 로직
    public void sendFriendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new RuntimeException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        Optional<Friendship> relation = friendshipRepository.findRelation(requesterId, receiverId);
        if (relation.isPresent()) {
            throw new RuntimeException("이미 친구이거나 요청 상태입니다.");
        }

        User requester = userRepository.findById(requesterId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        Friendship friendship = new Friendship();
        friendship.setRequester(requester);
        friendship.setReceiver(receiver);
        friendship.setStatus(Friendship.FriendStatus.PENDING);

        friendshipRepository.save(friendship);
    }

    public void respondRequest(Long friendshipId, boolean accept) {
        Friendship f = friendshipRepository.findById(friendshipId).orElseThrow();
        if (accept) f.setStatus(Friendship.FriendStatus.ACCEPTED);
        else friendshipRepository.delete(f);
    }

    public void changePassword(PasswordChangeRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow();
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword()))
            throw new RuntimeException("현재 비밀번호 불일치");
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    public void changeUsername(UsernameChangeRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow();
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호 불일치");

        if (userRepository.findByUsername(req.getNewUsername()).isPresent())
            throw new RuntimeException("이미 사용 중인 아이디입니다.");

        user.setUsername(req.getNewUsername());
        userRepository.save(user);
    }

    public List<SearchUserDto> searchUsers(Long myId, String keyword) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(keyword);

        return users.stream().map(user -> {
            UserProfile profile = profileRepository.findByUser_Id(user.getId());
            String imageUrl = (profile != null && profile.getProfileImageUrl() != null) ? profile.getProfileImageUrl() : DEFAULT_IMAGE;
            String relation = checkRelation(myId, user.getId());

            return new SearchUserDto(user.getId(), user.getUsername(), imageUrl, relation);
        }).toList();
    }

    public String checkRelation(Long myId, Long targetId) {
        if (myId.equals(targetId)) return "SELF";
        return friendshipRepository.findRelation(myId, targetId)
                .map(f -> f.getStatus().name()).orElse("NONE");
    }

    public void sendVerificationEmail(Long userId, String email) {
        UserProfile profile = profileRepository.findByUser_Id(userId);

        if (profile == null) {
            User user = userRepository.findById(userId).orElseThrow();
            profile = new UserProfile();
            profile.setUser(user);
            profile.setProfileImageUrl(DEFAULT_IMAGE);
        }

        // [추가됨] 이미 인증된 경우 차단
        if (profile.isEmailVerified()) {
            throw new RuntimeException("이미 이메일 인증이 완료된 계정입니다.");
        }

        String code = java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        profile.setVerificationCode(code);
        profileRepository.save(profile);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Agora 인증 코드");
        message.setText("인증번호: " + code);
        mailSender.send(message);
    }

    public void verifyEmailCode(EmailVerifyRequest req) {
        UserProfile profile = profileRepository.findByUser_Id(req.getUserId());
        if (profile == null) throw new RuntimeException("프로필이 없습니다.");

        if (profile.isEmailVerified()) {
            throw new RuntimeException("이미 인증이 완료되었습니다.");
        }

        if (profile.getVerificationCode() != null && profile.getVerificationCode().equals(req.getCode())) {
            profile.setEmailVerified(true);
            profile.setEmail(req.getEmail());
            profile.setVerificationCode(null);
            profileRepository.save(profile);
        } else {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }
    }
}