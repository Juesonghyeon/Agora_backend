package com.single.agora_backend.controller;

import com.single.agora_backend.dto.AuthRequest;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest req) {

        boolean success = userService.login(req.getUsername(), req.getPassword());
        if (!success) {
            throw new RuntimeException("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // userId 조회
        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 조회 실패"));

        // 프론트가 필요로 하는 정확한 응답 형태
        Map<String, Object> result = new HashMap<>();
        result.put("token", "LOCAL_TOKEN");  // JWT 없이 임시 토큰
        result.put("userId", user.getId());

        return result;
    }

    // 회원가입
    @PostMapping("/register")
    public String register(@RequestBody AuthRequest req) {
        userService.register(req.getUsername(), req.getPassword());
        return "회원가입 성공";
    }
}
