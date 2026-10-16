package com.example.demo1.dto;

public record LoginVO(
        String token,
        String tokenType,
        long expiresIn,
        UserVO user
) {
}
