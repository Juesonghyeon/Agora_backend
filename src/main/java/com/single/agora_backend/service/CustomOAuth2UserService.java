package com.single.agora_backend.service;

import com.single.agora_backend.entity.User;
import com.single.agora_backend.entity.UserProfile;
import com.single.agora_backend.repository.UserProfileRepository;
import com.single.agora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = extractEmail(registrationId, oAuth2User.getAttributes());

        UserProfile profile = userProfileRepository.findByEmail(email);
        User user;

        if (profile != null) {
            // 🌟 이미 등록된 이메일이면 기존 계정으로 연동 로그인!
            user = profile.getUser();
        } else {
            // 🌟 첫 로그인이면 임시 ID/PW 발급 후 회원가입 처리
            user = new User();
            user.setUsername(registrationId + "_" + UUID.randomUUID().toString().substring(0, 6));
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user = userRepository.save(user);

            profile = new UserProfile();
            profile.setUser(user);
            profile.setEmail(email);
            profile.setEmailVerified(true);
            profile.setProfileImageUrl("/uploads/profiles/default.jpg");
            userProfileRepository.save(profile);
        }

        return new DefaultOAuth2User(
                Collections.emptyList(),
                Map.of("email", email, "username", user.getUsername(), "userId", user.getId()),
                "email"
        );
    }

    private String extractEmail(String provider, Map<String, Object> attr) {
        if ("google".equals(provider)) return (String) attr.get("email");
        if ("naver".equals(provider)) return (String) ((Map)attr.get("response")).get("email");
        if ("discord".equals(provider)) return (String) attr.get("email");
        return null;
    }
}