package com.example.demo1.controller;

import com.example.demo1.annotation.RequireRole;
import com.example.demo1.common.BusinessException;
import com.example.demo1.common.Result;
import com.example.demo1.dto.UserVO;
import com.example.demo1.entity.User;
import com.example.demo1.enums.UserRole;
import com.example.demo1.service.UserService;
import com.example.demo1.util.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 受保护的用户接口：必须携带 Authorization: Bearer {token}
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        Long userId = UserContext.require().id();
        User user = userService.getById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("用户不存在或已被删除");
        }
        return Result.ok(UserVO.from(user));
    }

    @RequireRole(UserRole.ADMIN)
    @GetMapping
    public Result<List<UserVO>> list() {
        return Result.ok(userService.listAll().stream().map(UserVO::from).toList());
    }

    @RequireRole(UserRole.ADMIN)
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.ok();
    }
}
