package com.example.demo.controller;

import com.example.demo.dto.AuthInfo;
import com.example.demo.entity.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        return userService.login(email, password)
                .map(user -> {
                    String token = jwtService.encode(user.getId());
                    return ResponseEntity.ok("Bearer " + token);
                })
                .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // 프론트엔드에서 JWT를 삭제하는 방식으로 처리합니다.
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthInfo> getCurrentUser(AuthInfo authInfo) {
        return ResponseEntity.ok(authInfo);
    }
}