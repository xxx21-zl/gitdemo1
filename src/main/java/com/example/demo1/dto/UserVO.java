package com.example.demo1.dto;

import com.example.demo1.entity.User;
import com.example.demo1.enums.UserRole;

import java.time.LocalDateTime;

public record UserVO(
        Long id,
        String username,
        String nickname,
        String email,
        String phone,
        UserRole role,
        Integer status,
        LocalDateTime lastLoginTime,
        LocalDateTime createTime
) {

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getPhone(),
                user.getRole(), user.getStatus(), user.getLastLoginTime(), user.getCreateTime());
    }
}
