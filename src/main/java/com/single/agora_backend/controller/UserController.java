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
        // 디버깅 로그 추가
        System.out.println("로그인 시도 아이디: " + req.getUsername());
        System.out.println("로그인 시도 비번: " + req.getPassword());

        boolean success = userService.login(req.getUsername(), req.getPassword());

        if (!success) {
            // 여기서 에러가 난다면 DB의 암호화된 비번과 입력한 비번이 안 맞는 것입니다.
            throw new RuntimeException("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 조회 실패"));

        Map<String, Object> result = new HashMap<>();
        result.put("token", "LOCAL_TOKEN");
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
}
