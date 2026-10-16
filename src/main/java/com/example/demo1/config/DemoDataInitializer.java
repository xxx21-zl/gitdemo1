package com.example.demo1.config;

import com.example.demo1.entity.User;
import com.example.demo1.enums.UserRole;
import com.example.demo1.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始化演示账号（表为空时写入）
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createIfAbsent("admin", "admin123", "超级管理员", UserRole.ADMIN);
        createIfAbsent("test", "123456", "测试用户", UserRole.USER);
    }

    private void createIfAbsent(String username, String rawPassword, String nickname, UserRole role) {
        if (userService.findByUsername(username) != null) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNickname(nickname);
        user.setRole(role);
        user.setStatus(1);
        userService.save(user);
        log.info("初始化演示账号：{}/{}（角色 {}）", username, rawPassword, role.getCode());
    }
}
