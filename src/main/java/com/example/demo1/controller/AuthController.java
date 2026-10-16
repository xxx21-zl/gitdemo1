package com.example.demo1.controller;

import com.example.demo1.common.Result;
import com.example.demo1.dto.LoginRequest;
import com.example.demo1.dto.LoginVO;
import com.example.demo1.dto.RegisterRequest;
import com.example.demo1.dto.UserVO;
import com.example.demo1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口：注册 / 登录 / 登出，均无需 token
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(userService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(userService.login(request));
    }

    /**
     * JWT 是无状态的，服务端不需要销毁 token，前端删除本地 token 即可登出。
     * 若需要强制下线，可在 Redis 中维护 token 黑名单，由拦截器校验。
     */
    @GetMapping("/logout")
    public Result<String> logout() {
        return Result.ok("已登出，请删除本地 token");
    }
}
