package com.single.agora_backend.controller;

import com.single.agora_backend.config.JwtTokenProvider;
import com.single.agora_backend.dto.AuthRequest;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.service.UserService;
import lombok.RequiredArgsConstructor; // 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // 생성자 주입을 자동으로 처리해줍니다.
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest req) {
        boolean success = userService.login(req.getUsername(), req.getPassword());
        if (!success) {
            throw new RuntimeException("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 조회 실패"));

        Map<String, Object> result = new HashMap<>();

        // 🌟 "LOCAL_TOKEN" 가짜 문자열 대신 진짜 JWT 토큰 발급! (401 에러의 주범 해결)
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());

        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    // 회원가입
    @PostMapping("/register")
    public String register(@RequestBody AuthRequest req) {
        userService.register(req.getUsername(), req.getPassword());
        return "회원가입 성공";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            // 서버 콘솔에 구체적인 에러를 찍습니다. (SQL 오류 등)
            e.printStackTrace();
            return ResponseEntity.badRequest().body("탈퇴 실패: " + e.getMessage());
        }
    }
}